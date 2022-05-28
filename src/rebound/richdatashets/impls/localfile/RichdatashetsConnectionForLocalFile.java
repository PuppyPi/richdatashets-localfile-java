package rebound.richdatashets.impls.localfile;

import static java.util.Objects.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.file.FSUtilities;
import rebound.richdatashets.api.model.RichdatashetsTable;
import rebound.richdatashets.api.operation.RichdatashetsConnection;
import rebound.richdatashets.api.operation.RichdatashetsOperation;
import rebound.richdatashets.api.operation.RichdatashetsOperation.RichdatashetsOperationWithDataTimestamp;
import rebound.richdatashets.api.operation.RichdatashetsStructureException;
import rebound.richdatashets.api.operation.RichdatashetsUnencodableFormatException;

public class RichdatashetsConnectionForLocalFile
implements RichdatashetsConnection
{
	protected final @Nonnull File file;
	protected final @Nonnull RichdatashetsLocalFileFormatTranscoder fileFormat;
	
	public RichdatashetsConnectionForLocalFile(@Nonnull File file, @Nonnull RichdatashetsLocalFileFormatTranscoder fileFormat)
	{
		this.file = requireNonNull(file);
		this.fileFormat = requireNonNull(fileFormat);
	}
	
	public File getFile()
	{
		return file;
	}
	
	public RichdatashetsLocalFileFormatTranscoder getFileFormat()
	{
		return fileFormat;
	}
	
	
	
	@Override
	public Date getCurrentLastModifiedTimestamp() throws IOException
	{
		return file.isFile() ? new Date(file.lastModified()) : null;
	}
	
	
	
	@Override
	public void perform(boolean performMaintenance, RichdatashetsOperation operation) throws RichdatashetsStructureException, RichdatashetsUnencodableFormatException, IOException
	{
		//todolp actually use maxRowsToRead in this ^^'
		
		@Nullable RichdatashetsTable input;
		@Nullable Date lastModifiedTimeOfOriginalData;
		
		if (file.isFile())
		{
			lastModifiedTimeOfOriginalData = new Date(file.lastModified());
			
			try (InputStream in = new FileInputStream(file))
			{
				input = fileFormat.read(in);
			}
		}
		else
		{
			input = null;
			lastModifiedTimeOfOriginalData = null;
		}
		
		
		
		@Nullable RichdatashetsTable output = operation instanceof RichdatashetsOperationWithDataTimestamp ? ((RichdatashetsOperationWithDataTimestamp)operation).performInMemory(input, lastModifiedTimeOfOriginalData) : operation.performInMemory(input);
		
		
		
		if (output != null)
		{
			FSUtilities.performSafeFileSystemWriteTwoStageAndCopy(file, out ->
			{
				fileFormat.write(output, out);
			});
		}
	}
}
