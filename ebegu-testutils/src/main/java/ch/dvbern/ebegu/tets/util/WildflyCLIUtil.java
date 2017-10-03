/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
