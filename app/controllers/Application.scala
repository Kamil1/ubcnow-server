package controllers

import play.api._
import play.api.mvc._
import org.pac4j.core.profile._
import org.pac4j.play._
import org.pac4j.play.scala._
import play.api.libs.json.Json

object Application extends ScalaController {

  def index = Action { request =>
    val newSession = getOrCreateSessionId(request)
    val urlFacebook = getRedirectAction(request, newSession, "FacebookClient", "/?0").getLocation()
    val urlTwitter = getRedirectAction(request, newSession, "TwitterClient", "/?1").getLocation()
    val urlForm = getRedirectAction(request, newSession, "FormClient", "/?2").getLocation()
    val urlBA = getRedirectAction(request, newSession, "BasicAuthClient", "/?3").getLocation()
    val urlCas = getRedirectAction(request, newSession, "CasClient", "/?4").getLocation()
    val urlGoogleOpenId = getRedirectAction(request, newSession, "GoogleOpenIdClient", "/?5").getLocation()
    val urlSaml = getRedirectAction(request, newSession, "Saml2Client", "/?6").getLocation()
    val profile = getUserProfile(request)
    Ok(views.html.index(profile, urlFacebook, urlTwitter, urlForm, urlBA, urlCas, urlGoogleOpenId, urlSaml)).withSession(newSession)
  }

  def casIndex = RequiresAuthentication("CasClient") { profile =>
    Action { request =>
      Ok(views.html.protectedIndex(profile))
    }
  } 
}

