package org.madein;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.examples.util.Booter;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

public class MadeIn
{
	private List<URL> urls;
	
	private Object parent;
	
	public MadeIn() {
		urls = new ArrayList<URL>();
	}
	
	public MadeIn(Object parent) {
		urls = new ArrayList<URL>();
		this.parent = parent;
	}

	public ArtifactResult install(String mavenCoordinates, boolean resolve) throws ArtifactResolutionException {

		RepositorySystem system = newRepositorySystem();

		RepositorySystemSession session = Booter.newRepositorySystemSession( system );

		Artifact artifact = new DefaultArtifact( mavenCoordinates );

		ArtifactRequest artifactRequest = new ArtifactRequest();
		artifactRequest.setArtifact( artifact );
		artifactRequest.setRepositories( Booter.newRepositories( system, session ) );

		ArtifactResult artifactResult = system.resolveArtifact( session, artifactRequest );
		
		
        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        descriptorRequest.setArtifact( artifact );
        descriptorRequest.setRepositories( Booter.newRepositories( system, session ) );

        ArtifactDescriptorResult descriptorResult;
		try {
			descriptorResult = system.readArtifactDescriptor( session, descriptorRequest );
			
	        for (Dependency dependency : descriptorResult.getDependencies() )
	        {
	        	if (resolve) {
	        		ArtifactResult r = this.install( dependency.toString().replaceAll(" .*", ""), false );
	        		try {
						urls.add(new URL("file://"+r.getArtifact().getFile().toString()));
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		
	        		
	        		//System.out.println(dependency, false);
	        	}
	        	
	            //
	        }

	        
		} catch (ArtifactDescriptorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (resolve) {
			
			
	
		
		}

		return artifactResult;
	}

	private RepositorySystem newRepositorySystem()
	{
		/*
		 * Aether's components implement org.eclipse.aether.spi.locator.Service to ease manual wiring and using the
		 * prepopulated DefaultServiceLocator, we only need to register the repository connector and transporter
		 * factories.
		 */
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.addService( RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class );
		locator.addService( TransporterFactory.class, FileTransporterFactory.class );
		locator.addService( TransporterFactory.class, HttpTransporterFactory.class );

		locator.setErrorHandler( new DefaultServiceLocator.ErrorHandler()
		{
			@Override
			public void serviceCreationFailed( Class<?> type, Class<?> impl, Throwable exception )
			{
				exception.printStackTrace();
			}
		} );

		return locator.getService( RepositorySystem.class );
	}



	public ArtifactResult install(String mavenCoordinates) throws ArtifactResolutionException {
	
		ArtifactResult result  = this.install(mavenCoordinates, true);
		
	/*	try {
		    loader = new MadeinClassLoader(this.getUrls(), parent);
			
			loader.loadAndScanJar(result.getArtifact().getFile());
			
			loader.close();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		try {
			urls.add(result.getArtifact().getFile().toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public URL[] getUrls() {
		// TODO Auto-generated method stub
		return urls.toArray(new URL[urls.size()]);
	}
	
	
	public ClassLoader getClassLoader() {
		return URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]));
	}
	

}