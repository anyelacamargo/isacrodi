package org.isacrodi.diagnosis;

import org.junit.*;
import java.awt.image.*;
import java.awt.Image;
import java.io.*;
import java.sql.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Set;
import libsvm.*;


import org.isacrodi.ejb.entity.*;


public class DiagnosisTest
{
  private NumericType numericType;
  private NumericDescriptor numericDescriptor;
  private CategoricalType categoricalType;
  private CategoricalDescriptor categoricalDescriptor;
  private ImageType imageType;
  private ImageDescriptor imageDescriptor;
  private Crop tomato;
  private Crop aubergine;
  private CropDisorderRecord emptyCDR;
  private CropDisorderRecord cropDisorderRecord;
  private Set<Crop> cropSet;
  private Set<CropDisorder> cropDisorderSet;
  private Set<Procedure> procedureSet;
  private Recommendation recommendation;
  private Diagnosis diagnosis;


  @Before
  public void setUp() throws IOException
  {

    this.numericType = new NumericType("temperature");
    this.numericDescriptor = new NumericDescriptor();
    this.numericDescriptor.setId(11);
    this.numericDescriptor.setDescriptorType(this.numericType);
    this.numericDescriptor.setNumericValue(27.0);

    this.categoricalType = new CategoricalType("cropstage");
    CategoricalTypeValue categoricalTypeValue = new CategoricalTypeValue("vegetative");
    this.categoricalType.linkCategoricalTypeValue(categoricalTypeValue);
    this.categoricalType.linkCategoricalTypeValue(new CategoricalTypeValue("flowering"));
    this.categoricalType.linkCategoricalTypeValue(new CategoricalTypeValue("fruiting"));
    this.categoricalDescriptor = new CategoricalDescriptor(this.categoricalType);
    this.categoricalDescriptor.linkCategoricalTypeValue(categoricalTypeValue);

    this.imageType = new ImageType("leaf");
    this.imageDescriptor = new ImageDescriptor();
    this.imageDescriptor.setId(12);
    this.imageDescriptor.setDescriptorType(this.imageType);
    this.imageDescriptor.setMimeType("image/jpeg");
    String jpegFileName = "src/test/java/org/isacrodi/diagnosis/uchuva.jpg";
    File jpegFile = new File(jpegFileName);
    long jpegLength = jpegFile.length();
    byte[] jpegData = new byte[(int) jpegLength];
    FileInputStream f = new FileInputStream(jpegFile);
    if (f.read(jpegData) != (int) jpegLength)
    {
      throw new RuntimeException(String.format("failure to read image data from %s", jpegFileName));
    }
    this.imageDescriptor.setImageData(jpegData);

    //Create procedures
    Procedure proc1 = new Procedure("Water the plant");
    proc1.setId(1);
    Procedure proc2 = new Procedure("Prune the plant");
    proc2.setId(2);
    Procedure proc3 = new Procedure("Apply soapy water to leaves");
    proc3.setId(3);
    Procedure proc4 = new Procedure("Burn crop down");
    proc4.setId(4);
    Procedure proc5 = new Procedure("Apply eco-duff");
    proc5.setId(5);

    // Add first crop
    this.tomato = new Crop("Tomato", "Lycopersicon esculentum");
    this.tomato.setId(10);

    // Add crop disorder
    CropDisorder cdTom1 = new CropDisorder("anthracnose", "Colletotrichum coccodes");
    cdTom1.setId(1);
    cdTom1.setCropSet(new java.util.HashSet<Crop>());
    cdTom1.linkCrop(this.tomato);
    cdTom1.setProcedureSet(new java.util.HashSet<Procedure>());
    cdTom1.linkProcedure(proc1);
    cdTom1.linkProcedure(proc2);

    CropDisorder cdTom2 = new CropDisorder("anthracmouth", "Colletotrichum mouth");
    cdTom2.setId(2);
    cdTom2.setCropSet(new java.util.HashSet<Crop>());
    cdTom2.linkCrop(this.tomato);
    cdTom2.setProcedureSet(new java.util.HashSet<Procedure>());
    cdTom2.linkProcedure(proc1);

    CropDisorder cdTom3 = new CropDisorder("anthraclips", "Colletotrichum lips");
    cdTom3.setId(3);
    cdTom3.setCropSet(new java.util.HashSet<Crop>());
    cdTom3.linkCrop(this.tomato);
    cdTom3.setProcedureSet(new java.util.HashSet<Procedure>());
    cdTom3.linkProcedure(proc1);
    cdTom3.linkProcedure(proc2);
    cdTom3.linkProcedure(proc5);

    this.tomato.setCropDisorderSet(new java.util.HashSet<CropDisorder>());
    this.tomato.linkCropDisorder(cdTom1);
    this.tomato.linkCropDisorder(cdTom2);
    this.tomato.linkCropDisorder(cdTom3);

    // Add second crop
    this.aubergine = new Crop("Aubergine", "Solanum melongena");
    this.aubergine.setId(20);

    // Add crop disorder
    CropDisorder cdAub1 = new CropDisorder("Bacterial wilt", "Ralstonia (Pseudomonas) solanacearum");
    cdAub1.setId(4);
    cdAub1.setCropSet(new java.util.HashSet<Crop>());
    cdAub1.linkCrop(this.aubergine);
    cdAub1.setProcedureSet(new java.util.HashSet<Procedure>());
    cdAub1.linkProcedure(proc4);

    CropDisorder cdAub2 = new CropDisorder("Verticillium Wilt", "Verticillium sp");
    cdAub2.setId(5);
    cdAub2.setCropSet(new java.util.HashSet<Crop>());
    cdAub2.linkCrop(this.aubergine);
    cdAub2.setProcedureSet(new java.util.HashSet<Procedure>());
    cdAub2.linkProcedure(proc1);
    cdAub2.linkProcedure(proc5);

    this.aubergine.setCropDisorderSet(new java.util.HashSet<CropDisorder>());
    this.aubergine.linkCropDisorder(cdAub1);
    this.aubergine.linkCropDisorder(cdAub2);

    // Create crop set
    this.cropSet = new HashSet<Crop>();
    this.cropSet.add(this.tomato);
    this.cropSet.add(this.aubergine);
    this.cropDisorderSet = new HashSet<CropDisorder>();
    cropDisorderSet.add(cdTom1);
    cropDisorderSet.add(cdTom2);
    cropDisorderSet.add(cdTom3);
    cropDisorderSet.add(cdAub1);
    cropDisorderSet.add(cdAub2);

    // create empty CDR
    this.emptyCDR = new CropDisorderRecord();
    this.emptyCDR.setId(new Integer(1));

    // Create CDR
    this.cropDisorderRecord = new CropDisorderRecord();
    this.emptyCDR.setId(new Integer(2));
    this.cropDisorderRecord.setCrop(this.aubergine);
    this.cropDisorderRecord.setDescriptorSet(new java.util.HashSet<Descriptor>());
    this.cropDisorderRecord.linkDescriptor(this.numericDescriptor);
    this.cropDisorderRecord.linkDescriptor(this.categoricalDescriptor);
    this.cropDisorderRecord.linkDescriptor(this.imageDescriptor);
  }


  @Test(expected = IllegalStateException.class)
  public void testInvalidMimeType() throws IOException
  {
    ImageDescriptor idBroken = new ImageDescriptor();
    idBroken.setId(13);
    idBroken.setDescriptorType(this.imageType);
    idBroken.setMimeType("image/blah");
    BufferedImage bi = idBroken.bufferedImage();
  }


  @Test
  public void testImageFeatureExtractor() throws IOException
  {
    Assert.assertNotNull(this.imageDescriptor);
    Assert.assertNotNull(this.imageDescriptor.bufferedImage());
    ImageFeatureExtractor ife = new DummyImageFeatureExtractor();
    FeatureVector featureVector = ife.extract(this.cropDisorderRecord);
    Assert.assertNotNull(featureVector);
  }


  @Test
  public void testCDRFeatureExtractor() throws IOException
  {
    CDRFeatureExtractor cdrfe = new DummyCDRFeatureExtractor();
    FeatureVector featureVector = cdrfe.extract(this.cropDisorderRecord);
    System.err.println("testCDRFeatureExtractor: " + featureVector.toString());
    Assert.assertNotNull(featureVector);
  }


  @Test
  public void testDiagnosisStuff() throws IOException
  {
    DummyDiagnosisProvider dp = new DummyDiagnosisProvider();
    this.diagnosis = new Diagnosis();
    this.diagnosis.setId(1);
    this.diagnosis.setCropDisorderRecord(this.cropDisorderRecord);
    this.diagnosis.setDisorderScoreSet(new java.util.HashSet<DisorderScore>());
    this.cropDisorderRecord.setDiagnosis(this.diagnosis);

    for(Crop c : this.cropSet)
    {
      for(CropDisorder cd : c.getCropDisorderSet())
      {
        DisorderScore ds = new DisorderScore();
        ds.setDiagnosis(this.diagnosis);
	ds.setCropDisorder(cd);
        this.diagnosis.linkDisorderScore(ds);
      }
    }

    // instance variable diagnosis obsolescent...
    Diagnosis diagnosis = dp.diagnose(this.cropDisorderRecord, this.cropDisorderSet);
    // Test recommendation

    RecommendationProvider rp = new DummyRecommendationProvider();
    this.recommendation = new Recommendation();
    this.recommendation = rp.recommend(this.diagnosis);

    Assert.assertTrue(this.diagnosis != null);
  }

  /**
   * Test that all disorders get the same score when diagnosing an
   * empty CDR.
   *
   * @author jtk
   */
  @Test
  public void testEmptyDiagnosisProvider() throws IOException
  {
    Assert.assertTrue("no disorders", this.cropDisorderSet.size() > 0);
    DummyDiagnosisProvider dp = new DummyDiagnosisProvider();
    /* FIXME: test partially disabled pending refactoring of classification*/

    Diagnosis d = dp.diagnose(this.emptyCDR, this.cropDisorderSet);
    Assert.assertEquals(this.cropDisorderSet.size(), d.getDisorderScoreSet().size());
    double s = d.getDisorderScoreSet().iterator().next().getScore();
    for (DisorderScore ds : d.getDisorderScoreSet())
    {
      Assert.assertEquals(s, ds.getScore());
    }

  }


  /**
   * Test that only disorders that can affect the crop specified in
   * the CDR get a score greater than 0.
   *
   * @author jtk
   */
  @Test
  public void testDiagnosisProviderByCrop()
  {
    Assert.assertTrue("no disorders", this.cropDisorderSet.size() > 0);
    /* FIXME: partially disabled pending refactoring of classification*/

    DummyDiagnosisProvider dp = new DummyDiagnosisProvider();
    Diagnosis d = dp.diagnose(this.cropDisorderRecord, this.cropDisorderSet);
    for (DisorderScore ds : d.getDisorderScoreSet())
    {
      if (ds.getCropDisorder().getCropSet().contains(this.cropDisorderRecord.getCrop()))
      {
	Assert.assertTrue(ds.getScore() > 0.0);
      }
      else
      {
	Assert.assertEquals(0.0, ds.getScore());
      }
    }

  }


  /**
   * Test Feature Mapper
   *
   * @author avc
   */
  /* jtk: suspended test while redesign of serialising / parsing is pending
  @Test
  public void testSVMDiagnosis() throws IOException
  {
    String model_filename = "src/test/java/org/isacrodi/diagnosis/isacrodi_model";
    String parse_filename = "src/test/java/org/isacrodi/diagnosis/isacrodi_feature_mapper.txt";
    SVMDiagnosisProvider dp = new SVMDiagnosisProvider(model_filename, parse_filename);

    this.diagnosis = new Diagnosis();
    this.diagnosis.setId(1);
    this.diagnosis.setCropDisorderRecord(this.cropDisorderRecord);
    this.diagnosis.setDisorderScoreSet(new java.util.HashSet<DisorderScore>());
    this.cropDisorderRecord.setDiagnosis(this.diagnosis);

    for(Crop c : this.cropSet)
    {
      for(CropDisorder cd : c.getCropDisorderSet())
      {
        DisorderScore ds = new DisorderScore();
        ds.setDiagnosis(this.diagnosis);
	ds.setCropDisorder(cd);
        this.diagnosis.linkDisorderScore(ds);
      }
    }

    // instance variable diagnosis obsolescent...
    Diagnosis diagnosis = dp.diagnose(this.cropDisorderRecord);
    // Test recommendation

    //RecommendationProvider rp = new DummyRecommendationProvider();
    //this.recommendation = new Recommendation();
    //this.recommendation = rp.recommend(this.diagnosis);

    Assert.assertTrue(this.diagnosis != null);
  }
  */



  /**
   * Test straightforward mapping to arrays of {@code svm_node} (with
   * non-existent features simply not being mapped, using the sparse
   * vector approach provided by the {@code svm_node} structure.
   *
   * @author jtk
   */
  /* jtk: test disabled after introducing scaling
  @Test
  public void testSvmNodeMapping()
  {
    SvmNodeFeatureVectorMapper sfvm = new SvmNodeFeatureVectorMapper();
    sfvm.addComponentMapper(new NumericSvmNodeComponentMapper("temperature"));
    sfvm.addComponentMapper(new NumericSvmNodeComponentMapper("altitude"));
    CategoricalSvmNodeComponentMapper cm = new CategoricalSvmNodeComponentMapper("leafcondition");
    cm.addState("normal");
    cm.addState("crinkled");
    cm.addState("rotten");
    cm.addState("yellowish");
    sfvm.addComponentMapper(cm);
    sfvm.designateIndexes();
    FeatureVector fv = new FeatureVector();
    svm_node[] sn = sfvm.map(fv);
    Assert.assertEquals(sn.length, 0);
    fv.putFeature(new NumericFeature("temperature", 47.11));
    sn = sfvm.map(fv);
    Assert.assertEquals(sn.length, 1);
    fv.putFeature(new CategoricalFeature("leafcondition", "yellowish"));
    sn = sfvm.map(fv);
    Assert.assertEquals(sn.length, 5);
    fv.putFeature(new CategoricalFeature("leafcondition", "undefined"));
    sn = sfvm.map(fv);
    Assert.assertEquals(sn.length, 5);
    for (int i = 1; i < sn.length; i++)
    {
      Assert.assertEquals(sn[i].value, 0.0);
    }
    fv.putFeature(new NumericFeature("nonsense", -1111.11));
    sn = sfvm.map(fv);
    Assert.assertEquals(sn.length, 5);
    fv.putFeature(new NumericFeature("altitude", 123.45));
    sn = sfvm.map(fv);
    Assert.assertEquals(sn.length, 6);
  }
  */


  private static void dumpDisorderScoreSet(Collection<DisorderScore> disorderScoreSet)
  {
    for (DisorderScore ds : disorderScoreSet)
    {
      System.err.println(String.format("%s: %f", ds.getCropDisorder().getScientificName(), ds.getScore()));
    }
  }


  private static void dumpCropDisorderRecordSet(Collection<CropDisorderRecord> cropDisorderRecordSet)
  {
    for (CropDisorderRecord cdr : cropDisorderRecordSet)
    {
      System.err.println(cdr.fileRepresentation());
      System.err.println();
    }
  }


  @Test(expected = RuntimeException.class)
  public void testDiagnosisWithoutTraining()
  {
    SVMDiagnosisProvider sdp = new SVMDiagnosisProvider();
    sdp.diagnose(this.cropDisorderRecord, this.cropDisorderSet);
  }

  /**
   * Test the {@code SVMDiagnosisProvider}.
   *
   * <p>The test sets up an {@code altitude} numeric attribute, two
   * disorders, and a CDR set containing 5 CDRs expert diagnosed for
   * each disorder. Altitude attributes are in [1, 1.4] for CDRs with
   * the first disorder and in [10, 10.4] for the second, so the
   * disorders are easily linearly separable. Disorder scientific
   * names are also put in the CDR's descriptions for testing. Test
   * criteria are that</p>
   *
   * <ul>
   * <li>all training samples are classified correctly,</li>
   * <li>after switching scientific names of disorders, classification is still correct.</li>
   * </ul>
   *
   * <p>This test helps to ensure that diagnosis providers associate
   * disorders by scientific name and thus can be serialised for later
   * use in the context of different sets of known disorders.</p>
   */
  @Test
  public void testSvmDiagnosisProvider()
  {
    int id = 1;
    IsacrodiUser jtk = new IsacrodiUser("Kim", "Jan", "jtk", "*", "jttkim@gmail.com");
    NumericType altitude = new NumericType("altitude");
    altitude.setId(id++);
    Crop eggplant = new Crop("eggplant", "Solanum melongena");
    eggplant.setId(id++);
    CropDisorder disorder1 = new CropDisorder("whitefly", "Bemisia tabaci");
    disorder1.setId(id++);
    CropDisorder disorder2 = new CropDisorder("fall armyworm", "Spodoptera frugiperda");
    disorder2.setId(id++);
    HashSet<CropDisorder> cropDisorderSet = new HashSet<CropDisorder>();
    cropDisorderSet.add(disorder1);
    cropDisorderSet.add(disorder2);
    HashSet<CropDisorderRecord> cdrSet = new HashSet<CropDisorderRecord>();
    for (int i = 0; i < 5; i++)
    {
      CropDisorderRecord cdr1 = new CropDisorderRecord();
      cdr1.setId(id++);
      cdr1.setIsacrodiUser(jtk);
      cdr1.setCrop(eggplant);
      NumericDescriptor altitude1 = new NumericDescriptor(altitude, 1.0 + 0.1 * i);
      altitude1.setId(id++);
      cdr1.linkDescriptor(altitude1);
      cdr1.setExpertDiagnosedCropDisorder(disorder1);
      cdr1.setDescription(disorder1.getScientificName());
      cdrSet.add(cdr1);
      CropDisorderRecord cdr2 = new CropDisorderRecord();
      cdr2.setId(id++);
      cdr2.setIsacrodiUser(jtk);
      cdr2.setCrop(eggplant);
      NumericDescriptor altitude2 = new NumericDescriptor(altitude, 10.0 + 0.1 * i);
      altitude2.setId(id++);
      cdr2.linkDescriptor(altitude2);
      cdr2.setExpertDiagnosedCropDisorder(disorder2);
      cdr2.setDescription(disorder2.getScientificName());
      cdrSet.add(cdr2);
    }
    // dumpCropDisorderRecordSet(cdrSet);
    SVMDiagnosisProvider sdp = new SVMDiagnosisProvider();
    sdp.train(cdrSet, new Integer(1));
    for (CropDisorderRecord cdr : cdrSet)
    {
      Diagnosis diagnosis = sdp.diagnose(cdr, cropDisorderSet);
      // dumpDisorderScoreSet(diagnosis.getDisorderScoreSet());
      String sn = diagnosis.highestDisorderScore().getCropDisorder().getScientificName();
      Assert.assertEquals(cdr.getDescription(), sn);
    }
    disorder1.setName("fall armyworm");
    disorder1.setScientificName("Spodoptera frugiperda");
    disorder2.setName("whitefly");
    disorder2.setScientificName("Bemisia tabaci");
    for (CropDisorderRecord cdr : cdrSet)
    {
      Diagnosis diagnosis = sdp.diagnose(cdr, cropDisorderSet);
      // dumpDisorderScoreSet(diagnosis.getDisorderScoreSet());
      String sn = diagnosis.highestDisorderScore().getCropDisorder().getScientificName();
      Assert.assertEquals(cdr.getDescription(), sn);
    }
  }
}


