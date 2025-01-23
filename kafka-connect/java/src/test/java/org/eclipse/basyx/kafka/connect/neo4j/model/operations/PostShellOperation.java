package org.eclipse.basyx.kafka.connect.neo4j.model.operations;

import org.eclipse.basyx.kafka.connect.neo4j.docker.EnvironmentAccess;
import org.eclipse.basyx.kafka.connect.neo4j.util.SerializationTools;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;

import com.fasterxml.jackson.databind.JsonNode;

public class PostShellOperation extends IntegrationTestOperation {

	@Override
	public void execute(EnvironmentAccess access, SerializationTools iotools) throws Exception {
		JsonNode node = iotools.jsonMapper().valueToTree(getBody());
		AssetAdministrationShell shell = iotools.jsonDeserializer().read(node, AssetAdministrationShell.class);
		access.shells().post(shell);
	}
}
