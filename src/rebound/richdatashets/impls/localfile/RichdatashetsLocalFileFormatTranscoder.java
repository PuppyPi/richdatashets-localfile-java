package rebound.richdatashets.impls.localfile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import rebound.exceptions.BinarySyntaxException;
import rebound.richdatashets.api.model.RichdatashetsTable;
import rebound.richdatashets.api.operation.RichdatashetsUnencodableFormatException;

public interface RichdatashetsLocalFileFormatTranscoder
{
	public RichdatashetsTable read(InputStream in) throws IOException, BinarySyntaxException, RichdatashetsUnencodableFormatException;
	
	public void write(RichdatashetsTable data, OutputStream out) throws IOException, RichdatashetsUnencodableFormatException;
}
