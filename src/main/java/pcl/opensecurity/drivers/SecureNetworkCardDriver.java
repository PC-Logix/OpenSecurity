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

//Thanks gamax92
public class SecureNetworkCardDriver extends NetworkCard {

	public final EnvironmentHost container;
	private ComponentConnector node;

	public SecureNetworkCardDriver(EnvironmentHost container) {
		super(container);
		this.container = container;
		this.setNode(Network.newNode(this, Visibility.Network)
				.withComponent("modem", Visibility.Neighbors)
				.withConnector(10)
				.create());
	}

	@Override
	public Component node() {
		return this.node;
	}

	@Override
	protected void setNode(Node value) {
		if (value instanceof ComponentConnector)
			this.node = (ComponentConnector) value;
	}

	@Callback(doc = "function() -- Randomises the UUID")
	public Object[] generateUUID(Context context, Arguments args) {
		if(node.tryChangeBuffer(-1)) {
			Iterable<Node> tempNodes = this.node().neighbors();
			this.node().remove();
			this.setNode(Network.newNode(this, Visibility.Network)
					.withComponent("modem", Visibility.Neighbors)
					.withConnector(10)
					.create());
			Network.joinNewNetwork(this.node);
			Iterator<Node> meh = tempNodes.iterator();
			while (meh.hasNext()) {
				this.node().connect(meh.next());
			}
			return new Object[] { true };
		} else {
			return new Object[] { false };
		}
	}
}