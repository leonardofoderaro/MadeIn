package org.madein;

import org.eclipse.aether.resolution.ArtifactResult;

public class App {

	public static void main( String[] args )
			throws Exception {

		MavenDependencyInstaller installer = new MavenDependencyInstaller();

		ArtifactResult result = installer.install("org.apache.solr:solr-solrj:5.3.0");

		System.out.println("Artifact installed at " + result.getArtifact().getFile());
		
	}
}
