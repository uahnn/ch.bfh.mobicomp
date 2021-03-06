package examples;
import com.tinkerforge.IPConnection;

public class ExampleEnumerate {
	private static final String host = "localhost";
	private static final int port = 4223;

	// Note: To make the examples code cleaner we do not handle exceptions.
	// Exceptions you
	// might normally want to catch are described in the documentation
	public static void main(final String args[]) throws Exception {
		// Create connection and connect to brickd
		final IPConnection ipcon = new IPConnection();
		ipcon.connect(ExampleEnumerate.host, ExampleEnumerate.port);

		// Register enumerate listener and print incoming information
		ipcon.addEnumerateListener(new IPConnection.EnumerateListener() {
			@Override
			public void enumerate(final String uid, final String connectedUid,
					final char position, final short[] hardwareVersion,
					final short[] firmwareVersion, final int deviceIdentifier,
					final short enumerationType) {
				System.out.println("UID:               " + uid);
				System.out.println("Enumeration Type:  " + enumerationType);

				if (enumerationType == IPConnection.ENUMERATION_TYPE_DISCONNECTED) {
					System.out.println("");
					return;
				}

				System.out.println("Connected UID:     " + connectedUid);
				System.out.println("Position:          " + position);
				System.out.println("Hardware Version:  " + hardwareVersion[0]
						+ "." + hardwareVersion[1] + "." + hardwareVersion[2]);
				System.out.println("Firmware Version:  " + firmwareVersion[0]
						+ "." + firmwareVersion[1] + "." + firmwareVersion[2]);
				System.out.println("Device Identifier: " + deviceIdentifier);
				System.out.println("");
			}
		});

		ipcon.enumerate();

		System.console().readLine("Press key to exit\n");
		ipcon.disconnect();
	}
}
