# saflate  

Saflate (SAnitise-FLAky-TEsts) consists of JUnit5 extensions and backports using JUnit4 rules aimed at reducing test flakiness. This is generally achieved by inferring assumptions that define whether a test is meaningful in given configuration. This means that tests remain flaky in the sense that the outcome of their evaluation between test runs may still differ. However, instead of changing from *passed* to *failed* or vice versa across runs, the saflate extensions aim to sanitise tests that would otherwise fail, i.e. they will be reported as *skipped* instead of *failed*. This means that builds can still proceed as tests that are not meaningful in a certain configuration are sanitised on-the-fly. 

The initial modules implement this approach for flakiness caused by failing network connections. Check the readmes in the modules for more information how this can help your project.

The project has been supported by the [National Science Challenge of New Zealand -- Science for Technological Innovation (SFTI)](https://www.sftichallenge.govt.nz/).