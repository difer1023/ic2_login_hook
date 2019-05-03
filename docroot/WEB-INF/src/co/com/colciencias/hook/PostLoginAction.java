package co.com.colciencias.hook;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceException;

import co.com.ic2.colciencias.gruplac.ClasificacionGrupo;
import co.com.ic2.colciencias.utilidades.properties.ParametrosProperties;
import co.com.ic2.colciencias.utilidades.usuario.UsuarioUtil;
import co.com.ic2.facade.GrupoInvestigacionFacade;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.util.PortalUtil;

public class PostLoginAction extends Action {

	private static Log LOG = LogFactoryUtil.getLog(PostLoginAction.class);

	public void run(HttpServletRequest req, HttpServletResponse res) {
		LOG.info("Iniciando Login");

		User user = null;
		try {
			user = PortalUtil.getUser(req);
			PermissionChecker checker = PermissionCheckerFactoryUtil
					.create(user);
			PermissionThreadLocal.setPermissionChecker(checker);
		} catch (PortalException e) {
			LOG.error("Error obteniendo usuario liferay");
			e.printStackTrace();
		} catch (SystemException e) {
			LOG.error("Error obteniendo usuario liferay");
			e.printStackTrace();
		} catch (Exception e) {
			LOG.error("Error obteniendo usuario liferay");
			e.printStackTrace();
		}

		ParametrosProperties.getInstance().limpiarParametros();
		if (user.getExpandoBridge().getAttribute("codigoGrupo")!=null && user.getExpandoBridge().getAttribute("codigoGrupo") != "") {
			GrupoInvestigacionFacade facade = null;
			try {
				facade = new GrupoInvestigacionFacade();
			int anoFinVentanaObservacion = Integer
					.parseInt(ParametrosProperties.getInstance()
							.getPropiedadesPortal()
							.getProperty("anoFinVentanaObservacion"));

			 
			ClasificacionGrupo clasificacionGrupo=
			 facade.consultarGruposInvestigacion(Integer.parseInt((String)user.getExpandoBridge().getAttribute("codigoGrupo")),
			 anoFinVentanaObservacion);
			
			 HttpSession session = req.getSession(false);
			 session.setAttribute("clasificacionGrupoInvestigacion", clasificacionGrupo);
			 
			} catch (WebServiceException e) {
				LOG.error("Error en servicio grupos investigacion");
				e.printStackTrace();
			}

			try {
				
				if(!UsuarioUtil.INSTANCE.buscarRol(user.getRoles(),"Recomendacion")){
					res.sendRedirect("/group/user/seleccion-de-objetivo");
				}else if(!UsuarioUtil.INSTANCE.buscarRol(user.getRoles(),"UsuarioGrupo")){
					res.sendRedirect("/group/user/recomendacion");
				}else{
					res.sendRedirect("/group/user/inicio");
				}
			} catch (SystemException e1) {
				LOG.error("Error buscando rol");
				e1.printStackTrace();
			
			} catch (IOException e) {
				LOG.error("Error redireccionando a inicio");
				e.printStackTrace();
			}
		} else {
			try {
				LOG.info("Usuario sin codigo grupo");
				res.sendRedirect("/group/user/actualizacion-de-informacion");
			} catch (IOException e) {
				LOG.error("Error redireccionando a actualizacion de informacion");
				e.printStackTrace();
			}
		}
	}
}
