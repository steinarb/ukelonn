package no.priv.bang.ukelonn.web.security;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContext;

@Component(service=ServletContextHelper.class, immediate=true)
@HttpWhiteboardContext(name = "ukelonn", path = "/ukelonn")
public class UkelonnServletContextHelper extends ServletContextHelper { }
