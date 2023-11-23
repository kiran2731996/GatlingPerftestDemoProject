package SimulationNew

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

class PerfGatling_POST_GET_POC extends Simulation
{
  //protocol
  val httpProtocolconfig = http
    .baseUrl(url = "http://perf.academy.com")

  //scenarios
  def RecentlyViewed(): ChainBuilder = {

    exec(
        http(requestName = "Recentlyviewed.PG")
          .post(url = "/api/productinfo/recentlyviewed")
          .header(name = "content-type", value = "application/json")
          .asJson
          .body(RawFileBody("data/user.json")).asJson
          .check( bodyString.saveAs( "RESPONSE_DATA" ) )
          .check(
            status is 200,
            substring("profile")

          )

      )
      .exec(session => {
        println("Some Restful Service Response Body:")
        println(session("RESPONSE_DATA").as[String])
        session
      })
    }

  def Minicart() : ChainBuilder= {
    repeat(1) {
      exec(
        http(requestName = "Minicart.PG")
          .get("/api/cart/000000/summary")
          .header(name = "content-type", value = "application/json")
          .check(status is 200)
      )
    }
  }



  val sc1 = scenario("Recently-Viewed")
    .exec(RecentlyViewed())
    .pause(2)
    .exec(Minicart())
    .pause(2)


//  setUp(sc1.inject(atOnceUsers(users = 1))).protocols(httpProtocolconfig)
  setUp(sc1.inject(rampUsers(2).during(1.minutes))).maxDuration(5.minutes).protocols(httpProtocolconfig)
}