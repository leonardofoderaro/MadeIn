package org.madein;

import org.eclipse.aether.resolution.ArtifactResult;

public class MadeInApp {

	public static void main( String[] args ) throws Exception {

		// initalize the loader
		MavenDependencyInstaller madein = new MavenDependencyInstaller();

		// install a dependency
		ArtifactResult result = madein.install("org.apache.solr:solr-solrj:5.3.0");

		// we can show where it has been downloaded
		System.out.println("Artifact installed at " + result.getArtifact().getFile());
		
		// classes are automatically loaded & available
		Class documentClass = Class.forName("org.apache.solr.common.SolrDocument", false, madein.getClassLoader());
		
		Object doc = documentClass.newInstance();
		
		System.out.println(doc.toString());
		
	}
}
