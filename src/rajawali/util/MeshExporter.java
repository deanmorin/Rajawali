package rajawali.util;

import java.io.File;

import rajawali.BaseObject3D;
import rajawali.util.exporter.AExporter;
import android.os.Environment;

/**
 * Utility class for exporting models in various types. The exporter is flexible and should permit support of
 * essentially any file type. In the future the exporter will be updated to support scenes for AWD exporting and any
 * other model types that may support it.
 * <p>
 * 
 * <b>Example AWD exporting</b>
 * 
 * <code><pre>
 * 	BaseObject3D cube = new Cube(1f);
 * 	MeshExporter exporter = new MeshExporter(cube);
 * 	exporter.export(&quot;cube&quot;, AwdExporter.class);
 * </pre></code>
 * 
 * 
 * @author Ian Thomas (toxicbakery@gmail.com)
 * 
 */
public class MeshExporter {

	private BaseObject3D mObject;
	private String mFileName;
	private File mExportDir = null;
	private boolean mAppendExtension;

	public MeshExporter(BaseObject3D objectToExport) {
		mObject = objectToExport;
		mAppendExtension = true;
	}

	/**
	 * Export the model using the set exporter without compression. The filename is appended with the extension defined
	 * by the exporter if enabled.
	 * 
	 * @param fileName
	 * @param aExporter
	 * @throws ExporterException
	 */
	public void export(String fileName, Class<? extends AExporter> aExporter) throws ExporterException {
		export(fileName, aExporter, false);
	}

	/**
	 * Export the model using the set exporter and compression. The filename is appended with the extension defined by
	 * the exporter if enabled.
	 * 
	 * @param fileName
	 * @param aExporter
	 * @param compressed
	 * @throws ExporterException
	 */
	public void export(String fileName, Class<? extends AExporter> aExporter, boolean compressed)
			throws ExporterException {
		try {
			// Construct the parser
			final AExporter parser = (AExporter) Class.forName(aExporter.getName()).getConstructor()
					.newInstance();

			// Determine the output filename. Append the extension if flagged to do so.
			mFileName = mAppendExtension ? fileName + "." + parser.getExtension() : fileName;

			// Verify that the file exists or alternately can be written to.
			final File exportFile = getExportFile();

			// Create the file if it does not exist
			if (!exportFile.exists()) {
				exportFile.getParentFile().mkdirs();
				exportFile.createNewFile();
			}

			// Verify the file can be written to
			if (!exportFile.canWrite())
				throw new ExporterException(mFileName + " can not be written to.");

			// Configure the parser
			parser.setExporter(this);
			parser.setExportFile(getExportFile());
			parser.setExportModel(mObject);
			parser.setCompressed(compressed);
			configureExporter(parser);
			parser.export();
		} catch (Exception e) {
			throw new ExporterException(e);
		}
	}
	
	/**
	 * The file to be exported to.
	 * 
	 * @return
	 */
	public File getExportFile() {
		File path = mExportDir;
		if (mExportDir == null)
			path = Environment.getExternalStorageDirectory();

		return new File(path, mFileName);
	}

	/**
	 * Determine if the extension defined by the exporter should be automatically appended. This is true by default.
	 * 
	 * @param flag
	 */
	public void setAppendExtension(boolean flag) {
		mAppendExtension = flag;
	}

	/**
	 * Directory to export the model to.
	 * 
	 * @param exportDir
	 */
	public void setExportDirectory(File exportDir) {
		mExportDir = exportDir;
	}

	/**
	 * Method available for override to additionally configure a parser.
	 * 
	 * @param exporter
	 */
	protected void configureExporter(AExporter exporter) {}

	public static final class ExporterException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public ExporterException(final String msg) {
			super(msg);
		}

		public ExporterException(Throwable e) {
			super(e);
		}

		public ExporterException(final String msg, Throwable e) {
			super(msg, e);
		}

	}

}
