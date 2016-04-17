package pcl.opensecurity.drivers;

import java.util.Iterator;

import pcl.opensecurity.OpenSecurity;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.server.component.NetworkCard;

public class SecureNetworkCardDriver extends NetworkCard {

	public final li.cil.oc.api.network.EnvironmentHost container;
	private ComponentConnector node;

	public SecureNetworkCardDriver(li.cil.oc.api.network.EnvironmentHost arg1) {
		super(arg1);
		this.container = arg1;
		this.setNode(Network.newNode(this, Visibility.Network)
				.withComponent("modem", Visibility.Neighbors)
				.withConnector(1)
				.create());
	}

	@Callback(doc = "function() -- Randomises the UUID")
	public Object[] generateUUID(Context context, Arguments args) {
		//if(node.tryChangeBuffer(1)) {
		//<@Sangar> well, in that case your best bet is to store its neighbors before disconnecting, then reconnect to them all
		OpenSecurity.logger.info(this.node.address());
		Iterable<Node> tempNodes = this.node().neighbors();

		this.node.remove();
		this.node = Network.newNode(this, Visibility.Network)
				.withComponent("modem", Visibility.Neighbors)
				.withConnector(1)
				.create();
		
		Iterator<Node> meh = tempNodes.iterator();
		
		//OpenSecurity.logger.info(this.node());
		while (meh.hasNext()) {
			this.node().connect(meh.next());
		}
		
		OpenSecurity.logger.info(this.node.address());
		return new Object[] { true };
		//} else {
		//return new Object[] { false };
		//}
	}

	@Override
	public li.cil.oc.api.network.EnvironmentHost host() {
		return this.container;
	}

	@Override
	public Component node() {
		return this.node != null ? this.node : super.node();
	}

	@Override
	protected void setNode(Node value) {
		if(value instanceof ComponentConnector) {
			this.node = (ComponentConnector) value;
		}
		super.setNode(value);
	}
}
