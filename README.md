# MAven DEpendency INstaller
==

A small library to simplify the programmatic installation of Maven dependency to a local repository,
based on Eclipse Aether: http://git.eclipse.org/c/aether/

Build
==
mvn -Dmaven.test.skip=true package 

Example
==

        // initalize the loader
        MavenDependencyInstaller madein = new MavenDependencyInstaller();

        // install a dependency
        ArtifactResult result = madein.install("org.apache.solr:solr-solrj:5.3.0");

        // show where it has been downloaded
        System.out.println("Artifact installed at " + result.getArtifact().getFile());
		
        // classes are automatically loaded & available
        Class documentClass = Class.forName("org.apache.solr.common.SolrDocument", false, madein.getClassLoader());

        Object doc = documentClass.newInstance();
		
        System.out.println(doc.toString());



