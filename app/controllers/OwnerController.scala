package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import repository.SlickOwnerRepository
import play.api.data.Form
import play.api.data.Forms.{ mapping, text, number, ignored, nonEmptyText}
import models.Owner

case class Search(name: String)

object OwnerController extends Controller {

  def search = Action { implicit request =>
    Ok(views.html.findOwners())
  }
  
  def owner(id: Int) = Action { implicit request =>
  	val owner = SlickOwnerRepository.findOne(id)
  	Ok(views.html.owner(owner))
  }

  def list = Action { implicit request =>
    val name = searchForm.bindFromRequest
    name.fold(
      hasErrors => BadRequest,
      success = { filter =>
        val all = SlickOwnerRepository.findByLastName(filter.name)
        Ok(views.html.owners(all))
      })
  }
  
  def newOwnerForm = Action { implicit request =>
  	Ok(views.html.ownerNew(ownerForm))
  }
  
  def newOwner = Action { implicit request =>
  	val owner = ownerForm.bindFromRequest
  	owner.fold(
      hasErrors => BadRequest,
      success = { data =>
        val id = SlickOwnerRepository.save(data)
        Redirect(routes.OwnerController.owner(id))
      })
  }
  
  val ownerForm = Form(mapping(
      "id" -> ignored(None: Option[Int]),
      "first" -> nonEmptyText,
      "last" -> nonEmptyText,
      "address" -> nonEmptyText,
      "city" -> text,
      "phone" -> nonEmptyText)(Owner.apply)(Owner.unapply))

  val searchForm = Form(mapping("name" -> text)(Search.apply)(Search.unapply))
}