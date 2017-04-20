package pcl.opensecurity.drivers;

import java.util.Iterator;

import pcl.opensecurity.OpenSecurity;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.server.component.NetworkCard;

public class SecureNetworkCardDriver extends NetworkCard {

	public final EnvironmentHost container;

	public SecureNetworkCardDriver(EnvironmentHost container) {
		super(container);
		this.container = container;
		this.setNode(Network.newNode(this, Visibility.Network)
				.withComponent("modem", Visibility.Neighbors)
				.withConnector(10)
				.create());
	}

	@Callback(doc = "function() -- Randomises the UUID")
	public Object[] generateUUID(Context context, Arguments args) {
		//if(node.tryChangeBuffer(-1)) {
			//<@Sangar> well, in that case your best bet is to store its neighbors before disconnecting, then reconnect to them all
			String oldNode = this.node().address();
			OpenSecurity.logger.info("Old: " + this.node().address());
			Iterable<Node> tempNodes = this.node().neighbors();

			this.node().remove();
			this.setNode(Network.newNode(this, Visibility.Network)
					.withComponent("modem", Visibility.Neighbors)
					.withConnector(10)
					.create());

			Iterator<Node> meh = tempNodes.iterator();
			while (meh.hasNext()) {
				Node thisNode = meh.next();
				System.out.println(thisNode.address());
				this.node().connect(thisNode);
			}

			OpenSecurity.logger.info("New: " + this.node().address());
			return new Object[] { true };
		//} else {
		//	return new Object[] { false };
		//}
	}
}
