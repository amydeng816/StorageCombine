package main.java.com.storagecombine;

import java.util.Arrays;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
//import com.google.api.services.drive.model.File;



import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.model.FileList;

import java.util.List;
import java.util.ArrayList;


public class GoogleDriveStorage implements StorageService {
	private String GD_STORAGE_DIR = "";
	private static final String GD_ID = "927321522546-g4rk0l9sbadld28jccaah4q80ofa2o0f.apps.googleusercontent.com";
	private static final String GD_SECRET = "bNtk5uuqxui6NH6LOhGS2QCb";
	private static String GD_REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

	private Credentials cred = null;
	private Drive client = null;

	public GoogleDriveStorage(Credentials c) {
		this.cred = c;

		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();

		GoogleCredential credential = new GoogleCredential().setAccessToken(c.getToken());
		client  = new Drive.Builder(httpTransport, jsonFactory, credential).build();
		//setApplicationName(Config.CLOUD_VAULT_IDENTIFIER);
	}

	public File get(String filename) {
		File newFile = new File(Config.MERGE_DIR + "/" + filename);
		FileOutputStream outputStream = null;

		try {
			List<com.google.api.services.drive.model.File> result = new ArrayList<com.google.api.services.drive.model.File>();
			Files.List request = client.files().list();

			do {
				com.google.api.services.drive.model.FileList files = request.execute();

				result.addAll(files.getItems());
				request.setPageToken(files.getNextPageToken());
			} while (request.getPageToken() != null && request.getPageToken().length() > 0);


			for (com.google.api.services.drive.model.File f : result) {

				if (f.getTitle().equals(filename)) {
					HttpResponse resp = client.getRequestFactory().buildGetRequest(new GenericUrl(f.getDownloadUrl())).execute();
					outputStream = new FileOutputStream(newFile, true);

					byte[] buffer = new byte[1024];
					int len;
					while ((len = resp.getContent().read(buffer)) != -1) {
						outputStream.write(buffer, 0, len);
					}

					outputStream.flush();
					outputStream.close();

					return newFile;
				}
			}
		} catch (Exception e) { e.printStackTrace(); return null;}

		return null;
	}

	public boolean put(File file) {
		try {
			com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
			body.setTitle(file.getName());
			body.setDescription("CloudVault data");
			FileContent mediaContent = new FileContent("*/*", file);

			com.google.api.services.drive.model.File file2 = client.files().insert(body, mediaContent).execute();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean del(String filename) {
		try {
			// TODO: Implement deletion
			//client.delete("/" + filename);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public long space() {
		// TODO: Implement space of local storage (get free space)
		return 0;
	}
}
