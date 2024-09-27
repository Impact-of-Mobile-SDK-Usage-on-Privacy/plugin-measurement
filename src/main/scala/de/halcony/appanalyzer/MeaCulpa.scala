package de.halcony.appanalyzer

import de.halcony.appanalyzer.AppStates.{AppState, BASIC_FUNC, CONSENT, CREATE_OBJECT, INIT, NOTHING, BASIC_FUNC_INF}

import scala.annotation.nowarn
import de.halcony.appanalyzer.analysis.interaction.{Interface, InterfaceElementInteraction}
import de.halcony.appanalyzer.analysis.plugin.ActorPlugin
import de.halcony.appanalyzer.platform.appium.Appium
import de.halcony.appanalyzer.analysis.Analysis
import de.halcony.appanalyzer.analysis.exceptions.AnalysisFatal
import de.halcony.appanalyzer.analysis.interaction.InteractionTypes.PRESS
import wvlet.log.LogSupport

import scala.io.StdIn.readLine

object AppStates extends Enumeration {
  type AppState = Value
  val NOTHING, CREATE_OBJECT, INIT, CONSENT, BASIC_FUNC, BASIC_FUNC_INF = Value
}

class MeaCulpa() extends ActorPlugin with LogSupport {

  private var actions : List[AppState] = List()
  private var time : Option[Long] = None
  private var appium : Boolean = true

  /** the plugin expects parameters for both the action order and the type of sdk
   *
   * @param parameter the parameters
   * @return this
   */
  override def setParameter(parameter: Map[String, String]): ActorPlugin = {
    appium = parameter.getOrElse("appium","true").toBoolean
    parameter.get("actions") match {
      case Some(value) =>
        actions = value.split(";").map(_.toLowerCase).map {
          case "init" => INIT
          case "create" => CREATE_OBJECT
          case "consent" => CONSENT
          case "nothing" => NOTHING
          case "basic" => BASIC_FUNC
          case "basic_inf" => BASIC_FUNC_INF
          case x => throw AnalysisFatal(s"the action $x is unknown")
        }.toList
      case None => throw AnalysisFatal("the actions of the app need to be provided")
    }
    /*atype = Some(parameter.get("type") match {
      case Some("analytics") => ANALYTICS
      case Some("advertisement") => ADVERTISEMENT
      case _ => throw AnalysisFatal("the type of the analyzed SDK needs to be provided")
    })*/
    time = Some(parameter.getOrElse("time","60000").toLong)
    this
  }

  override def getDescription: String = "Analyzing SDK"

  private def measurementWithAppium(interface : Interface, context : Analysis, appium : Appium) : Option[InterfaceElementInteraction] = {
    // get the current and the remaining actions
    val (current: AppState) :: remaining = this.actions
    this.actions = remaining
    val currentInterface = interface
    //info(s"available buttons ${currentInterface.getElements.mkString(",")}")
    current match {
      case BASIC_FUNC_INF =>
        info(s"we are currently looking at $current and will continue whenever you press ENTER")
        readLine("Press ENTER to continue ...")
      case x =>
        info(s"we are currently looking at $x and will continue in ${time.get}ms")
        Thread.sleep(time.get)
    }
    context.stopTrafficCollection()
    // if there are actions remaining
    if (remaining.nonEmpty) {
      context.startTrafficCollection(None, remaining.head.toString)
      // prompt the PI for app interaction
      info(s"please continue in the app and then insert any key to continue the measurement")
      // and to confirm interaction via ENTER
      readLine("Press ENTER to continue ...")
    } else {
      // otherwise we do nothing and this will also end the measurement and remove the app
    }
    // if we are not at the final action
    if (remaining.nonEmpty) {
      // then we return the current interaction
      val button = currentInterface.getElements.find(keyval => keyval._1.getText.toLowerCase().contains("next")).get._2
      Some(new InterfaceElementInteraction(PRESS, currentInterface, button, Some(Interface(context, appium, flat = false, remaining.head.toString)))) // tell the analysis that you are done
    } else {
      // else we just return none to show that we are done
      None
    }
  }

  private def measurementWithoutAppium(@nowarn interface : Interface, context : Analysis,@nowarn appium : Appium) : Option[InterfaceElementInteraction] = {
    // while there are actions remaining
    while(actions.nonEmpty) {
      // get the action currently active
      val current :: rest = this.actions
      // and remove it from the remaining action list
      this.actions = rest
      // tell the PI what we are currently measuring
      current match {
        case BASIC_FUNC_INF =>
          info(s"we are currently looking at $current and will continue whenever you press ENTER")
          readLine("Press ENTER to continue ...")
        case x =>
          info(s"we are currently looking at $x and will continue in ${time.get}ms")
          Thread.sleep(time.get)
      }
      //info(s"we are currently looking at $action and will continue in ${time.get}ms")
      // wait for the configured wait time
      //Thread.sleep(time.get)
      // stop traffic collection
      context.stopTrafficCollection()
      // if there are actions remaining further steps are required
      if (rest.nonEmpty) {
        // restart traffic collection as the next action
        context.startTrafficCollection(None, rest.head.toString)
        // prompt the PI for app interaction
        info(s"please continue in the app and then insert any key to continue the measurement")
        // and to confirm interaction via ENTER
        readLine("Press ENTER to continue ...")
      }
    }
    None
  }

  override def action(interface: Interface)(implicit context: Analysis, appium: Appium): Option[InterfaceElementInteraction] = {
    context.checkIfAppIsStillRunning(true)
    if(this.appium) {
      measurementWithAppium(interface, context, appium)
    } else {
      measurementWithoutAppium(interface, context, appium)
    }
  }

  /** check if actor wants to run again on the same app
   *
   * the first element indicates if the actor wants to run on the same app again
   * the second element indicates if the app should be reset before restarting
   *
   * @return
   */
  override def restartApp: (Boolean, Boolean) = {
    // as long as there are actions left we want to continue our traffic collection
    (this.actions.nonEmpty,false)
  }

  override def onAppStartup(implicit context: Analysis): Unit = {
    val current :: _ = this.actions
    context.startTrafficCollection(None,current.toString)
  }
}
