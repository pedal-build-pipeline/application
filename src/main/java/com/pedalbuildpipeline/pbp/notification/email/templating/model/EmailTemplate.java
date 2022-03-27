package com.pedalbuildpipeline.pbp.notification.email.templating.model;

import com.github.jknack.handlebars.Template;

public record EmailTemplate(Template htmlTemplate, Template textTemplate) {}
