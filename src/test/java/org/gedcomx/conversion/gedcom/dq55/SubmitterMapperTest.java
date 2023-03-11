package org.gedcomx.conversion.gedcom.dq55;

import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.GedcomTag;
import org.folg.gedcom.model.Submitter;
import org.folg.gedcom.parser.ModelParser;
import org.gedcomx.agent.Address;
import org.gedcomx.agent.Agent;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class SubmitterMapperTest {
  private Gedcom gedcom;

  @BeforeClass
  public void setUp() throws Exception {
    URL gedcomUrl = this.getClass().getClassLoader().getResource("Case008-SubmitterRecord.ged");
    File gedcomFile = new File(gedcomUrl.toURI());
    ModelParser modelParser = new ModelParser();

    gedcom = modelParser.parseGedcom(gedcomFile);
    assertNotNull(gedcom);
    assertNotNull(gedcom.getSubmitters().get(0));
  }

  @Test
  public void testToContributor1() throws Exception {
    Submitter dqSubmitter = gedcom.getSubmitters().get(0);
    TestConversionResult result = new TestConversionResult();
    SubmitterMapper mapper = new SubmitterMapper();

    // assert parts of the structure that give us code coverage but that are dropped
    // by conversion process
    assertEquals(dqSubmitter.getValue(), "__spec_deviation__value__");
    assertEquals(dqSubmitter.getRin(), "12345");
    assertEquals(((List<GedcomTag>) dqSubmitter.getExtensions().get("folg.more_tags")).size(), 1);
    assertEquals(((List<GedcomTag>) dqSubmitter.getExtensions().get("folg.more_tags")).get(0).getTag(), "UID");
    assertEquals(((List<GedcomTag>) dqSubmitter.getExtensions().get("folg.more_tags")).get(0).getValue(), "23456");

    // execute conversion and test conversion outcome
    mapper.toContributor(dqSubmitter, result);
    assertNotNull(result.getContributors());
    assertEquals(result.getContributors().size(), 1);
    Agent gedxPerson = result.getContributors().get(0);
    assertEquals(gedxPerson.getId(), "SUBM1");
    assertEquals(gedxPerson.getName().getValue(), "Henri Herkimer Hofmeir");
    for (Address address : gedxPerson.getAddresses()) {
      assertEquals(address.getValue(), "1 Genealogist Way\n" +
          "Hometown, ZZ  99999\n" +
          "United States");
      assertNull(address.getStreet());
      assertNull(address.getStreet2());
      assertNull(address.getStreet3());
      assertNull(address.getCity());
      assertNull(address.getStateOrProvince());
      assertNull(address.getPostalCode());
      assertNull(address.getCountry());
    }
    assertEquals(Arrays.toString(gedxPerson.getPhones().toArray()),
        "[data:,Phone:%20935-555-1212, data:,Fax:%20935-555-0101]");
    assertEquals(Arrays.toString(gedxPerson.getEmails().toArray()), "[mailto:info@nospam.com]");
    assertEquals(gedxPerson.getHomepage().getResource().toString(), "http://nospam.com/");

    assertNull(gedxPerson.getAccounts());
    assertNull(gedxPerson.getOpenid());
  }
}
