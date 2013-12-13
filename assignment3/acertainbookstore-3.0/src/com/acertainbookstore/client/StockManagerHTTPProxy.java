/**
 * 
 */
package com.acertainbookstore.client;

import java.util.List;
import java.util.Set;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.acertainbookstore.business.BookCopy;
import com.acertainbookstore.business.BookEditorPick;
import com.acertainbookstore.business.StockBook;
import com.acertainbookstore.interfaces.StockManager;
import com.acertainbookstore.utils.BookStoreException;
import com.acertainbookstore.utils.BookStoreMessageTag;
import com.acertainbookstore.utils.BookStoreUtility;

/**
 * StockManagerHTTPProxy implements the client level synchronous
 * CertainBookStore API declared in the BookStore class
 * 
 * Uses the HTTP protocol for communication with the server
 * 
 */
public class StockManagerHTTPProxy implements StockManager {
	protected HttpClient client;
	protected String serverAddress;

	/**
	 * Initialize the client object
	 */
	public StockManagerHTTPProxy(String serverAddress) throws Exception {
		setServerAddress(serverAddress);
		client = new HttpClient();
		client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		client.setMaxConnectionsPerAddress(BookStoreClientConstants.CLIENT_MAX_CONNECTION_ADDRESS); // max
																									// concurrent
																									// connections
																									// to
																									// every
																									// address
		client.setThreadPool(new QueuedThreadPool(
				BookStoreClientConstants.CLIENT_MAX_THREADSPOOL_THREADS)); // max
																			// threads
		client.setTimeout(BookStoreClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS); // seconds
																					// timeout;
																					// if
																					// no
																					// server
																					// reply,
																					// the
																					// request
																					// expires
		client.start();
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public synchronized void addBooks(Set<StockBook> bookSet)
			throws BookStoreException {
		ContentExchange exchange = new ContentExchange();
		String urlString;
		urlString = serverAddress + "/" + BookStoreMessageTag.ADDBOOKS;

		String listBooksxmlString = BookStoreUtility
				.serializeObjectToXMLString(bookSet);
		exchange.setMethod("POST");
		exchange.setURL(urlString);
		Buffer requestContent = new ByteArrayBuffer(listBooksxmlString);
		exchange.setRequestContent(requestContent);

		BookStoreUtility.SendAndRecv(this.client, exchange);
	}

	public void addCopies(Set<BookCopy> bookCopiesSet)
			throws BookStoreException {
		ContentExchange exchange = new ContentExchange();

		String urlString;
		urlString = serverAddress + "/" + BookStoreMessageTag.ADDCOPIES;

		String listBookCopiesxmlString = BookStoreUtility
				.serializeObjectToXMLString(bookCopiesSet);
		exchange.setMethod("POST");
		exchange.setURL(urlString);
		Buffer requestContent = new ByteArrayBuffer(listBookCopiesxmlString);
		exchange.setRequestContent(requestContent);

		BookStoreUtility.SendAndRecv(this.client, exchange);
	}

	@SuppressWarnings("unchecked")
	public synchronized List<StockBook> getBooks() throws BookStoreException {
		ContentExchange exchange = new ContentExchange();
		String urlString = serverAddress + "/" + BookStoreMessageTag.LISTBOOKS;

		exchange.setURL(urlString);

		return (List<StockBook>) BookStoreUtility.SendAndRecv(this.client,
				exchange);
	}

	public void updateEditorPicks(Set<BookEditorPick> editorPicksValues)
			throws BookStoreException {
		ContentExchange exchange = new ContentExchange();
		String urlString = serverAddress + "/"
				+ BookStoreMessageTag.UPDATEEDITORPICKS + "?";

		String xmlStringEditorPicksValues = BookStoreUtility
				.serializeObjectToXMLString(editorPicksValues);

		exchange.setMethod("POST");
		exchange.setURL(urlString);
		Buffer requestContent = new ByteArrayBuffer(xmlStringEditorPicksValues);

		exchange.setRequestContent(requestContent);

		BookStoreUtility.SendAndRecv(this.client, exchange);

	}

    public void resetInstance() throws BookStoreException {
        ContentExchange exchange = new ContentExchange();
        String urlString = serverAddress + "/" + BookStoreMessageTag.RESETINSTANCE;

        exchange.setURL(urlString);

        BookStoreUtility.SendAndRecv(this.client, exchange);

        return;
    }

	public void stop() {
		try {
			client.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<StockBook> getBooksInDemand() throws BookStoreException {
		// TODO Auto-generated method stub
		throw new BookStoreException();
	}

}
