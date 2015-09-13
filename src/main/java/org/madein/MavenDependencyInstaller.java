package org.madein;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

public class MavenDependencyInstaller
{
	
	private List<URL> urls;
	
	public MavenDependencyInstaller() {
		urls = new ArrayList<URL>();
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
	        		System.out.println(r.getArtifact().getFile());
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

	public static void main( String[] args )
			throws Exception {

		MavenDependencyInstaller installer = new MavenDependencyInstaller();

		ArtifactResult result = installer.install("redis.clients:jedis:2.7.3", true);

		System.out.println("Artifact installed in " + result.getArtifact().getFile());
		
		URL[] urls = new URL[1];
		urls[0] = new URL("file://"+result.getArtifact().getFile());
		
		JarScanner scanner = new JarScanner(installer.getUrls());
		scanner.loadAndScanJar(result.getArtifact().getFile());
		

	}

	private URL[] getUrls() {
		// TODO Auto-generated method stub
		return urls.toArray(new URL[urls.size()]);
	}

}