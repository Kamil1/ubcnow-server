package controllers

import scala.concurrent.Future
import play.api._
import play.api.Play.current
import play.api.mvc._
import org.pac4j.play._
import org.pac4j.core.client._
import org.pac4j.cas.client._
import play.api.mvc.Results._


object Global extends GlobalSettings {

  override def onError(request: RequestHeader, t: Throwable) = {
    Future.successful(InternalServerError(
      views.html.error500.render()
    ))
}

  override def onStart(app: Application) {
    Config.setErrorPage401(views.html.error401.render().toString())
    Config.setErrorPage403(views.html.error403.render().toString())

    val baseUrl = Play.application.configuration.getString("baseUrl").get
    val casUrl = Play.application.configuration.getString("casUrl").get

  // CAS
  val casClient = new CasClient();
  // casClient.setLogoutHandler(new PlayLogoutHandler());
  // casClient.setCasProtocol(CasProtocol.SAML);
  // casClient.setGateway(true);
  /*final CasProxyReceptor casProxyReceptor = new CasProxyReceptor();
  casProxyReceptor.setCallbackUrl("http://localhost:9000/casProxyCallback");  // Where do we take the user after they log in?
  casClient.setCasProxyReceptor(casProxyReceptor);*/
  casClient.setCasLoginUrl(casUrl);

  val clients = new Clients(baseUrl + "/callback", casClient);
  Config.setClients(clients);
}
}
