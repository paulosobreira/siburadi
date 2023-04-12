package sowbreira.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectCloner {
	// so that nobody can accidentally create an ObjectCloner object
	private ObjectCloner() {
	}

	// returns a deep copy of an object
	static public Object deepCopy(Object oldObj) throws Exception {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;

		try {
			Thread.currentThread().setPriority(Thread.NORM_PRIORITY - 2);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(); // A
			oos = new ObjectOutputStream(bos); // B

			// serialize and pass the object
			oos.writeObject(oldObj); // C
			oos.flush(); // D

			ByteArrayInputStream bin = new ByteArrayInputStream(
					bos.toByteArray()); // E
			ois = new ObjectInputStream(bin); // F

			// return the new object
			return ois.readObject(); // G
		} catch (Exception e) {
			e.printStackTrace();
			throw (e);
		} finally {
			oos.close();
			ois.close();
			Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
		}
	}
}
