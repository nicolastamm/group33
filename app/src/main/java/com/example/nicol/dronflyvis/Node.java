package com.example.nicol.dronflyvis;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Johannes
 * @author Martin
 * @author Heiko (implementation of parcelable)
 */
public class Node implements Parcelable
{
    private double longitude;
    private double latitude;
    private int positionFlag;

    /**
     * Sets the Latitude, the Longitude and the positionFlag of a Node
     * @param latitude to set
     * @param longitude to set
     */
    Node(double latitude, double longitude , int positionFlag)
    {
        setLongitude(longitude);
        setLatitude(latitude);
        setPositionFlag(positionFlag); // 0 -> normal; 1 -> borderNode ; 2 -> User input Edges;
    }

    protected Node(Parcel in)
    {
        longitude = in.readDouble();
        latitude = in.readDouble();
        positionFlag = in.readInt();
    }

    public static final Creator<Node> CREATOR = new Creator<Node>() {
        @Override
        public Node createFromParcel(Parcel in) {
            return new Node(in);
        }

        @Override
        public Node[] newArray(int size) {
            return new Node[size];
        }
    };

    /**
     * @param positionFlag the flag to set
     */
    public void setPositionFlag(int positionFlag)
    {
        this.positionFlag = positionFlag;
    }

    /**
     * @return the position flag
     */
    public int getPositionFlag()
    {
        return positionFlag;
    }

    /**
     * @return the longitude
     */
    public double getLongitude()
    {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    /**
     * @return the latitude
     */
    public double getLatitude()
    {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    /**
     * Compare the latitude of this Node with an other Node
     * @param compare the Node to compare with this Node
     * @return true iff the latitude of this Node is greater than the latitude of the Node to compare
     */
    public boolean gth(Node compare)
    {
        return this.latitude > compare.getLatitude();
    }

    /**
     * Compare the latitude of this Node with an other Node
     * @param compare the Node to compare with this Node
     * @return true iff the latitude of this Node is lower than the latitude of the Node to compare
     */
    public boolean lth(Node compare)
    {
        return this.latitude < compare.getLatitude();
    }

    /**
     * Compare the latitude of this Node with an other Node
     * @param compare the Node to compare with this Node
     * @return true iff the latitude of this Node is greater equal than the latitude of the Node to compare
     */
    public boolean geq(Node compare)
    {
        return this.latitude >= compare.getLatitude();
    }

    /**
     * Compare the latitude of this Node with an other Node
     * @param compare the Node to compare with this Node
     * @return true iff the latitude of this Node is lower equal than the latitude of the Node to compare
     */
    public boolean leq(Node compare)
    {
        return this.latitude <= compare.getLatitude();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "latitude=" + latitude + " longitude=" + longitude + " flag=" + positionFlag /* ", positionFlag="+ positionFlags +*/;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
        parcel.writeInt(positionFlag);
    }
}
