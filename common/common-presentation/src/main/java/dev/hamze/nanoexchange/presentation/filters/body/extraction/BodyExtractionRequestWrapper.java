package dev.hamze.nanoexchange.presentation.filters.body.extraction;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BodyExtractionRequestWrapper extends ContentCachingRequestWrapper {

    private static final Logger logger = LoggerFactory.getLogger(BodyExtractionRequestWrapper.class);

    public BodyExtractionRequestWrapper(HttpServletRequest request) {
        super(request);
        readInputStream();
    }

    public BodyExtractionRequestWrapper(HttpServletRequest request, int contentCacheLimit) {
        super(request, contentCacheLimit);
        readInputStream();
    }

    @Override
    public ServletInputStream getInputStream() {
        return new ContentReadingAndCachingInputStream(getContentAsByteArray());
    }

    private void readInputStream() {
        try {
            byte[] requestPayloadBytes = IOUtils.toByteArray(super.getInputStream());
        } catch (IOException ex) {
            logger.warn("Unable to read and cache request body", ex);
        }
    }

    private static class ContentReadingAndCachingInputStream extends ServletInputStream {

        private final InputStream delegate;

        public ContentReadingAndCachingInputStream(byte[] body) {
            this.delegate = new ByteArrayInputStream(body);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return this.delegate.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return this.delegate.read(b, off, len);
        }

        @Override
        public int read(byte[] b) throws IOException {
            return this.delegate.read(b);
        }

        @Override
        public long skip(long n) throws IOException {
            return this.delegate.skip(n);
        }

        @Override
        public int available() throws IOException {
            return this.delegate.available();
        }

        @Override
        public void close() throws IOException {
            this.delegate.close();
        }

        @Override
        public synchronized void mark(int readlimit) {
            this.delegate.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            this.delegate.reset();
        }

        @Override
        public boolean markSupported() {
            return this.delegate.markSupported();
        }
    }
}
