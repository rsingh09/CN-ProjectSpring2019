package trialSelector;

public class CommonProperties {
    public static int numberOfPreferredNeighbors;
    public static int unchokingInterval;
    public static int optimisticUnchokingInterval;
    public static String fileName;
    public static int fileSize;
    public static int pieceSize;
	/**
	 * @return the numberOfPreferredNeighbors
	 */
	public static int getNumberOfPreferredNeighbors() {
		return numberOfPreferredNeighbors;
	}
	/**
	 * @param numberOfPreferredNeighbors the numberOfPreferredNeighbors to set
	 */
	public static void setNumberOfPreferredNeighbors(int numberOfPreferredNeighbors) {
		CommonProperties.numberOfPreferredNeighbors = numberOfPreferredNeighbors;
	}
	/**
	 * @return the unchokingInterval
	 */
	public static int getUnchokingInterval() {
		return unchokingInterval;
	}
	/**
	 * @param unchokingInterval the unchokingInterval to set
	 */
	public static void setUnchokingInterval(int unchokingInterval) {
		CommonProperties.unchokingInterval = unchokingInterval;
	}
	/**
	 * @return the optimisticUnchokingInterval
	 */
	public static int getOptimisticUnchokingInterval() {
		return optimisticUnchokingInterval;
	}
	/**
	 * @param optimisticUnchokingInterval the optimisticUnchokingInterval to set
	 */
	public static void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) {
		CommonProperties.optimisticUnchokingInterval = optimisticUnchokingInterval;
	}
	/**
	 * @return the fileName
	 */
	public static String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public static void setFileName(String fileName) {
		CommonProperties.fileName = fileName;
	}
	/**
	 * @return the fileSize
	 */
	public static int getFileSize() {
		return fileSize;
	}
	/**
	 * @param fileSize the fileSize to set
	 */
	public static void setFileSize(int fileSize) {
		CommonProperties.fileSize = fileSize;
	}
	/**
	 * @return the pieceSize
	 */
	public static int getPieceSize() {
		return pieceSize;
	}
	/**
	 * @param pieceSize the pieceSize to set
	 */
	public static void setPieceSize(int pieceSize) {
		CommonProperties.pieceSize = pieceSize;
	}
}

