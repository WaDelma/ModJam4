package delma.colormod.color;

public class TankIdentifier {
	public final int dimID;
	public final int x, y, z;
	public final int index;

	public TankIdentifier(int x, int y, int z, int index, int dimID) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.index = index;
		this.dimID = dimID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dimID;
		result = prime * result + index;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TankIdentifier other = (TankIdentifier) obj;
		if (dimID != other.dimID) {
			return false;
		}
		if (index != other.index) {
			return false;
		}
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		if (z != other.z) {
			return false;
		}
		return true;
	}

}