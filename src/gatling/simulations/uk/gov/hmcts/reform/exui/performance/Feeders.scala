package uk.gov.hmcts.reform.exui.performance

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Random

object Feeders {

  val repeat  = List(1, 2, 3,4,5)
  var generatedEmail = ""
  var generatedPassword = ""
  var generatedEmailForCase = ""
  var orgName = ""
  var appReferenceName = ""
  var sequence1 = 0
  var seq = 1

  val rnd = new Random()
  val now = LocalDate.now()
  val patternDay = DateTimeFormatter.ofPattern("dd")
  val patternMonth = DateTimeFormatter.ofPattern("MM")
  val patternYear = DateTimeFormatter.ofPattern("yyyy")

  def sequenceValue() =
    Stream.continually(repeat.toStream).flatten.take(5).toList

  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def nextSeq() : Integer = {
    seq = seq + 1
        seq
    }

  def generatenextNumber() :Integer = {
    sequence1 = (sequenceValue().iterator.next())
    sequence1
  }

  def generateOrganisationName() :String = {
    orgName = ("pforgdiv-" + randomString(5))
    orgName
  }

  def generateEmailAddress() :String = {
    generatedEmail = (generateOrganisationName() + "_superuser@mailinator.com")
    generatedEmail
  }

  def generateUserEmailAddress() :String = {
    generatedEmail = (generateOrganisationName() + "_user"+"@mailtest.gov.uk")
    generatedEmail
  }

  def generateEmailForCase() :String = {
    generatedEmailForCase = ("exui_case" + randomString(6) + "@mailtest.gov.uk")
    generatedEmailForCase
  }

  def generateAppReferenceName() :String = {
    appReferenceName = ("case ref perftest" + randomString(6))
    appReferenceName
  }

  def generatePassword() :String = {
    generatedPassword = "Pass19word"
    generatedPassword
  }

  def getDay(): String = {
    (1 + rnd.nextInt(28)).toString.format(patternDay).reverse.padTo(2, '0').reverse //pads single-digit dates with a leading zero
  }

  def getMonth(): String = {
    (1 + rnd.nextInt(12)).toString.format(patternMonth).reverse.padTo(2, '0').reverse //pads single-digit dates with a leading zero
  }

  //Dob >= 35 years
  def getDobYear(): String = {
    now.minusYears(35 + rnd.nextInt(70)).format(patternYear)
  }
  //Dod <= 21 years
  def getDodYear(): String = {
    now.minusYears(1 + rnd.nextInt(20)).format(patternYear)
  }

  val createDynamicDataFeeder = Iterator.continually(Map("generatedEmail" -> ({
    generateOrganisationName()+"-su@mailtest.gov.uk"
  }),
    "orgName" -> ({
    orgName
  })
  ));
  /*val createDynamicDataFeeder = Iterator.continually(Map("generatedEmail" -> (generatedEmail), "generatedPassword" -> (generatedPassword), "generateOrganisationName" -> (orgName)));
*/

  val createCaseData = Iterator.continually(Map("caseEmail" -> ({
    generateEmailForCase()
  }),"appRef" -> ({
    generateAppReferenceName()
  })));

  val createDynamicUserDataFeeder = Iterator.continually(Map("generatedUserEmail" -> ({
    "-user"
  })));

  val IACCreateDataFeeder = Iterator.continually(Map("service" -> ({
    "IACC"
  }),
    "SignoutNumber" -> ({
      "280"
    })

  ));

  val FPLCreateDataFeeder = Iterator.continually(Map("service" -> ({
    "FPLC"
  }),
    "SignoutNumber" -> ({
      "390"
    })
  ));

  val IACViewDataFeeder = Iterator.continually(Map("service" -> ({
    "IACV"
  }),
    "SignoutNumber" -> ({
      "070"
    })
  ));

  val FPLViewDataFeeder = Iterator.continually(Map("service" -> ({
    "FPLV"
  }),
    "SignoutNumber" -> ({
      "070"
    })
  ));

  val FPLSDODataFeeder = Iterator.continually(Map("service" -> ({
    "SDO"
  }),
    "SignoutNumberAdmin" -> ({
      "150"
    }),
    "SignoutNumberGK" -> ({
      "290"
    })

  ));

  val ProDataFeeder = Iterator.continually(Map("service" -> ({
    "PROB"
  }),
    "SignoutNumber" -> ({
      "310"
    })
  ));

  val CwDataFeeder = Iterator.continually(Map("service" -> ({
    "CW"
}),
    "SignoutNumber" -> ({
      "130"
    })
  ));

  val DivDataFeeder = Iterator.continually(Map("service" -> ({
    "DIV"
  }),
    "SignoutNumber" -> ({
      "220"
    })
  ));

  val FRApplicantDataFeeder = Iterator.continually(Map("service" -> ({
    "FR_Applicant"
  }),
    "SignoutNumber" -> ({
      "230"
    })
  ));

  val FRRespondentDataFeeder = Iterator.continually(Map("service" -> ({
    "FR_Respondent"
  }),
    "SignoutNumber" -> ({
      "080"
    })
  ));

}



