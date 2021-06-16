package uk.gov.hmcts.reform.exui.performance

import uk.gov.hmcts.reform.exui.performance.scenarios.utils.Common

object Feeders {

  val orgName = "pforgdiv-" + Common.randomString(5)

  val createDynamicDataFeeder = Iterator.continually(Map(
    "generatedEmail" -> (orgName + "-su@mailtest.gov.uk"),
    "organisationName" -> orgName))

  val FPLCreateDataFeeder = Iterator.continually(Map("service" -> ({
    "FPLC"
  }),
    "SignoutNumber" -> ({
      "390"
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



