package ule.chat.utils;

import lombok.SneakyThrows;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CachedBodyServletInputStream extends ServletInputStream {
	private final InputStream cachedBodyInputStream;

	public CachedBodyServletInputStream(byte[] cachedBody) {
		this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
	}

	@SneakyThrows(IOException.class)
	@Override
	public boolean isFinished() {
		return this.cachedBodyInputStream.available() == 0;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setReadListener(ReadListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int read() throws IOException {
		return cachedBodyInputStream.read();
	}
}
