package dim.scrapper.olx.exception;

import java.io.IOException;

public class ResponseCodeException extends IOException {

	public ResponseCodeException(String name){
		super(name);
	}
}
