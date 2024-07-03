### ET Multiples Data Creation Gatling Scripts

This script contains three simulations, which can be called from build.gradle:
- Create an ET multiple case
- Create ET singles cases
- Add ET singles cases to an ET multiple case

The three simulations should typically each be run in isolation, and output data will need
to be manually moved to the necessary input files.

**ETCreateMultiple_Simulation**

Creates one ET singles case (to be the lead case), 
one ET multiple case with the lead case assigned, and then writes the new multiple CCD case id 
to a file 'casesCreatedMultiples.csv' 
- Update build.gradle to run the ETCreateMultiple_Simulation simulation
- Inputs: User credentials defined in 'UserDataETMultiples.csv' (multiple) and 'UserDataET.csv' (single lead case)
- Outputs: Multiple CCD Case Id is written to 'casesCreatedMultiples.csv'

**ETCreateSingles_Simulation**

Createe ET singles cases, progress them to an 'Accepted' state
(so they may be added to a multiple), and write the Ethos Ids to a file 'casesCreatedSingles.csv'
- Update build.gradle to run the ETCreateSingles_Simulation simulation
- Inputs: User credentials defined in 'UserDataET.csv'; numberOfSinglesToCreatePerUser; numberOfUsers
- Outputs: Singles Ethos Ids are written to 'casesCreatedSingles.csv'

**ETAddSinglesToMultiple_Simulation**

Adds singles to a provided multiple case in batches of a given size using their Ethos Ids
- Update build.gradle to run the ETAddSinglesToMultiple_Simulation simulation
- Inputs: User credentials defined in 'UserDataETMultiples.csv'; 'singlesEthodIdsToAddToMultiple.csv' containing a list of 
singles Ethos Ids to add to the multiple; 'multipleCaseId.csv' containing the multiple CCD Case ID;
desiredBatchSize (in the 'ETAddSinglesToMultiple' scenario) defining the number of singles to add to the multiple case per API call to CCD
- Outputs: Multiple case will be updated with the new singles and a new Case Name to reflect the total 
number of singles assigned e.g. 'ET Multiple (10)'
- Note 1: all singles cases in 'singlesEthodIdsToAddToMultiple.csv' will be added to the multiple. There is no functionality 
at present to add a subset
- Note 2: if a multiple case with 10 singles are required, only add 9 singles when using this simulation, as the lead single 
is created as part of the multiple case creation

**How to Run**

To run:
- Run against the perftest environment: `./gradlew gatlingRun`

Before running, update the client secret in src/gatling/resources/application.conf then run `git update-index --assume-unchanged src/gatling/resources/application.conf` to ensure the changes aren't pushed to github.

To make other configuration changes to the file, first run `git update-index --no-assume-unchanged src/gatling/resources/application.conf`, ensuring to remove the client secret before pushing to origin