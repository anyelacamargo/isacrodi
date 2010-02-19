package org.isacrodi.ejb.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.Set;


@Entity
public class Recommendation implements Serializable
{
  private Integer id;
  private int version;
  private CropDisorderRecord cropDisorderRecord;
  private Set<Procedure> procedure;

  private static final long serialVersionUID = 1;


  Recommendation()
  {
    super();
  }

  @Id
  @GeneratedValue
  public Integer getId()
  {
    return id;
  }


  public void setId(Integer id)
  {
    this.id = id;
  }


  @Version
  public int getVersion()
  {
    return (this.version);
  }


  public void setVersion(int version)
  {
    this.version = version;
  }


  @OneToOne
  public CropDisorderRecord getCropDisorderRecord()
  {
    return (this.cropDisorderRecord);
  }


  public void setCropDisorderRecord( CropDisorderRecord cropDisorderRecord)
  {
    this.cropDisorderRecord = cropDisorderRecord;
  }

 
   
  public Set<Procedure> getProcedure()
  {
    return procedure;
  }

 
  public void setProcedure(Set<Procedure> procedure)
  {
    this.procedure = procedure;
  }

}
