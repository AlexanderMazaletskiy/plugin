package it.pmx.atlassian.plugins.servlet;

import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;

@Named("userMySkillsServlet")
public class MySkillsUserServlet extends HttpServlet {

	private static final String MY_SKILLS_PROFILE_VM_VIEW = "view/profile/my-skills-profile.vm";

	/**
	 * 
	 */
	private static final long serialVersionUID = -3045826976673753230L;

	private static final Logger log = LoggerFactory.getLogger(MySkillsUserServlet.class);

	@ComponentImport
	private final UserManager userManager;
	@ComponentImport
	private final LoginUriProvider loginUriProvider;
	@ComponentImport
	private final TemplateRenderer templateRenderer;

	@Inject
	public MySkillsUserServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer templateRenderer) {

		this.userManager = userManager;
		this.loginUriProvider = loginUriProvider;
		this.templateRenderer = templateRenderer;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UserProfile userProfile = userManager.getRemoteUser(req);
		if (userProfile == null || !userManager.isSystemAdmin(userProfile.getUserKey())) {
			redirectToLogin(req, resp);
			return;
		}
		templateRenderer.render(MY_SKILLS_PROFILE_VM_VIEW, resp.getWriter());
	}

	private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
	}

	private URI getUri(HttpServletRequest request) {
		StringBuffer builder = request.getRequestURL();
		if (request.getQueryString() != null) {
			builder.append("?");
			builder.append(request.getQueryString());
		}
		return URI.create(builder.toString());
	}

}
