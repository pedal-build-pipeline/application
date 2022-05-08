import mjml2html from 'mjml';
import glob from 'glob';
import fs from 'fs/promises';
import path from 'path';
import {convert} from 'html-to-text';

console.log('Begin processing MJML templates');

const getFiles = async () => {
    return new Promise((resolve, reject) => {
        glob('src/main/resources/**/*.mjml', (err, files) => {
            if (err) {
                reject(err);
            } else {
                resolve(files);
            }
        });
    });
};

const transformFile = async (file) => {
    console.log('Processing file: ', file);

    const mjml = (await fs.readFile(file)).toString();

    const html = mjml2html(mjml).html;

    const text = convert(html);

    return {
        html, text
    };
};

const writeFiles = async (transformedFiles) => {
    await Promise.all(
        await Promise.all(transformedFiles.map((file) => {
            const dirName = `build/resources/main${path.dirname(file.file).replace('src/main/resources', '')}`;
            const baseName = path.basename(file.file, '.mjml');

            console.log(`Writing file: ${file.file} to ${dirName}/${baseName}.**.hbs`, file.file);

            return fs.mkdir(dirName, {recursive: true}).then(() => {
                return [
                    fs.writeFile(`${dirName}/${baseName}.html.hbs`, file.transformed.html),
                    fs.writeFile(`${dirName}/${baseName}.text.hbs`, file.transformed.text)
                ]
            });
        }).flat())
    );
};

const transformFiles = async (files) => {
    return await Promise.all(files.map(async (file) => {
        return {
            file,
            transformed: await transformFile(file)
        };
    }));
};

const files = await getFiles();

console.log('Begin transforming files: ', files);

const transformedFiles = await transformFiles(files);

console.log('Begin writing files');

await writeFiles(transformedFiles);