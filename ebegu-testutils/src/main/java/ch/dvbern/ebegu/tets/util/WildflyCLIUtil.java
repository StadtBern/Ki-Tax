package ch.dvbern.ebegu.tets.util;

import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dominik Pospisil <dpospisi@redhat.com>
 * @see org.jboss.as.test.integration.management.ManagementOperations
 */
public class WildflyCLIUtil {

	private static final Logger LOG = LoggerFactory.getLogger(WildflyCLIUtil.class);

    public static List<String> modelNodeAsStringList(ModelNode node) {
		List<String> ret = new LinkedList<>();
        for (ModelNode n : node.asList()) {
			ret.add(n.asString());
		}
        return ret;
    }

    public static ModelNode createCompositeNode(ModelNode... steps) {
        ModelNode comp = new ModelNode();
        comp.get("operation").set("composite");
        for (ModelNode step : steps) {
            comp.get("steps").add(step);
        }
        return comp;
    }

    public static boolean execute(ManagementClient client, ModelNode operation) {
        try {
            ModelNode result = client.getControllerClient().execute(operation);
            return isOperationSuccess(result);
        } catch (IOException e) {
			LOG.error("Failed execution of operation " + operation.toJSONString(false), e);
            return false;
        }
    }

    private static boolean isOperationSuccess(ModelNode node) {

        boolean success = "success".equalsIgnoreCase(node.get("outcome").asString());
        if (!success) {
            LOG.error("Operation failed with \n{}", node.toJSONString(false));
        }

        return success;

    }

    public static ModelNode createOpNode(String address, String operation) {
        ModelNode op = new ModelNode();

        // set address
        ModelNode list = op.get("address").setEmptyList();
        if (address != null) {
            String[] pathSegments = address.split("/");
            for (String segment : pathSegments) {
                String[] elements = segment.split("=");
                list.add(elements[0], elements[1]);
            }
        }
        op.get("operation").set(operation);
        return op;
    }
}
