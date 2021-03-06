package org.drools.kproject;

import com.thoughtworks.xstream.XStream;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KProjectImpl implements KProject {

    private GroupArtifactVersion groupArtifactVersion;
    
    // qualifier to path
    private String              kProjectPath;
    private String              kBasesPath;

    private Map<String, KBase>  kBases;
    
    private  transient PropertyChangeListener listener;
    
    public KProjectImpl() {
        kBases = Collections.emptyMap();
    }    

    public GroupArtifactVersion getGroupArtifactVersion() {
        return groupArtifactVersion;
    }

    public void setGroupArtifactVersion(GroupArtifactVersion groupArtifactVersion) {
        this.groupArtifactVersion = groupArtifactVersion;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#getListener()
     */
    public PropertyChangeListener getListener() {
        return listener;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#setListener(java.beans.PropertyChangeListener)
     */
    public KProject setListener(PropertyChangeListener listener) {
        this.listener = listener;
        for ( KBase kbase : kBases.values() ) {
            // make sure the listener is set for each kbase
            kbase.setListener( listener );
        }
        return this;
    }



    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#getKProjectPath()
     */
    public String getKProjectPath() {
        return kProjectPath;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#setKProjectPath(java.lang.String)
     */
    public KProject setKProjectPath(String kprojectPath) {
        if ( listener != null ) {
            listener.propertyChange( new java.beans.PropertyChangeEvent( this, "kProjectPath", this.kProjectPath, kProjectPath ) );
        }
        this.kProjectPath = kprojectPath;
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#getKBasesPath()
     */
    public String getKBasesPath() {
        return kBasesPath;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#setKBasesPath(java.lang.String)
     */
    public KProject setKBasesPath(String kprojectPath) {
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "kBasesPath", this.kBasesPath, kBasesPath ) );     
        }
        this.kBasesPath = kprojectPath;
        return this;
    }  
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#addKBase(org.kie.kproject.KBaseImpl)
     */
    public KBase newKBase(String namespace,
                         String name) {
        KBase kbase = new KBaseImpl(this, namespace, name);
        Map<String, KBase> newMap = new HashMap<String, KBase>();
        newMap.putAll( this.kBases );        
        newMap.put( kbase.getQName(), kbase );
        setKBases( newMap );   
        
        return kbase;
    }
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#removeKBase(org.kie.kproject.KBase)
     */
    public void removeKBase(String qName) {
        Map<String, KBase> newMap = new HashMap<String, KBase>();
        newMap.putAll( this.kBases );
        newMap.remove( qName );
        setKBases( newMap );
    }    
    
    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#removeKBase(org.kie.kproject.KBase)
     */
    public void moveKBase(String oldQName, String newQName) {
        Map<String, KBase> newMap = new HashMap<String, KBase>();
        newMap.putAll( this.kBases );
        KBase kBase = newMap.remove( oldQName );
        newMap.put( newQName, kBase );
        setKBases( newMap );
    }        

    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#getKBases()
     */
    public Map<String, KBase> getKBases() {
        return Collections.unmodifiableMap( kBases );
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#setKBases(java.util.Map)
     */
    private void setKBases(Map<String, KBase> kBases) {        
        if ( listener != null ) {
            listener.propertyChange( new PropertyChangeEvent( this, "kBases", this.kBases, kBases ) );
            
            for ( KBase kbase : kBases.values() ) {
                // make sure the listener is set for each kbase
                kbase.setListener( listener );
            }
        }
        
        this.kBases = kBases;
    }

    List<String> validate() {
        List<String> problems = new ArrayList<String>();
        if ( kProjectPath == null) {
            problems.add( "A path to the kproject.properties file must be specified" );
        }
//
//        // check valid kbase relative paths
//        for ( Entry<String, String> entry : kbasePaths.entrySet() ) {
//
//        }

        // validate valid kbases
        //for ( Entry<String, >)

        return problems;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KProject#toString()
     */
    @Override
    public String toString() {
        return "KProject [kprojectPath=" + kProjectPath + ", kbases=" + kBases + "]";
    }

    public String toXML() {
        return new XStream().toXML(this);
    }

    public static KProject fromXML(InputStream kProjectStream) {
        return (KProject)new XStream().fromXML(kProjectStream);
    }

    public static KProject fromXML(java.io.File kProjectFile) {
        return (KProject)new XStream().fromXML(kProjectFile);
    }

    public static KProject fromXML(URL kProjectUrl) {
        return (KProject)new XStream().fromXML(kProjectUrl);
    }
}
