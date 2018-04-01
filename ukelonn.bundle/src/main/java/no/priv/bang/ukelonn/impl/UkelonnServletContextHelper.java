package no.priv.bang.ukelonn.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

@Component(
    property= {
        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME+"=ukelonn",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH+"=/ukelonn"},
    service=ServletContextHelper.class,
    immediate=true
)
public class UkelonnServletContextHelper extends ServletContextHelper { }
