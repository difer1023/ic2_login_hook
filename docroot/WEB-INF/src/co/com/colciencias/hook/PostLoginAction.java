package co.com.colciencias.hook;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceException;

import co.com.ic2.colciencias.gruplac.ClasificacionGrupo;
import co.com.ic2.colciencias.utilidades.properties.ParametrosProperties;
import co.com.ic2.facade.GrupoInvestigacionFacade;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.util.PortalUtil;

public class PostLoginAction extends Action {

	// private ThemeDisplay themeDisplay;

	public void run(HttpServletRequest req, HttpServletResponse res) {
		System.out.println("## My custom login action");

		// themeDisplay=(ThemeDisplay) req.getAttribute(WebKeys.THEME_DISPLAY);
		User user = null;
		try {
			user = PortalUtil.getUser(req);
			PermissionChecker checker = PermissionCheckerFactoryUtil
					.create(user);
			PermissionThreadLocal.setPermissionChecker(checker);
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ParametrosProperties.getInstance().limpiarParametros();
		if (user.getExpandoBridge().getAttribute("codigoGrupo") != "") {
			// Usuario usuario=new Usuario();
			// usuario.setCodigoGrupo(Integer.parseInt((String)user.getExpandoBridge().getAttribute("codigoGrupo")));
			GrupoInvestigacionFacade facade = null;
			try {
				facade = new GrupoInvestigacionFacade();
			} catch (WebServiceException e) {

				e.printStackTrace();
			}
			int anoFinVentanaObservacion = Integer
					.parseInt(ParametrosProperties.getInstance()
							.getPropiedadesPortal()
							.getProperty("anoFinVentanaObservacion"));

			 
			ClasificacionGrupo clasificacionGrupo=
			 facade.consultarGruposInvestigacion(Integer.parseInt((String)user.getExpandoBridge().getAttribute("codigoGrupo")),
			 anoFinVentanaObservacion);
			
//			MultiVMPoolUtil.getCache("GrupoInvestigacion").put((String)user.getExpandoBridge().getAttribute("codigoGrupo"),clasificacionGrupo);
			// usuario.setRecomendacion((String)user.getExpandoBridge().getAttribute("recomendacion"));
			//
			//
			//
			 HttpSession session = req.getSession(false);
			 // PortalUtil.getPortletSession
			 session.setAttribute("clasificacionGrupoInvestigacion", clasificacionGrupo);

			try {
				res.sendRedirect("/group/user/inicio");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				res.sendRedirect("/group/user/actualizacion-de-informacion");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
