package freenet.clients.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.sql.Date;
import java.text.ParseException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.Log;

import freenet.client.HighLevelSimpleClient;
import freenet.support.MultiValueTable;
import freenet.support.api.Bucket;
import freenet.support.api.HTTPRequest;

/** This toadlet creates a PNG image with the specified text. */
public class ImageCreatorToadlet extends Toadlet {

	/** The default width */
	public static final int		DEFAULT_WIDTH	= 100;

	/** The default height */
	public static final int		DEFAULT_HEIGHT	= 100;

	/**
	 * The last modification time of the class, it is required for the
	 * client-side cache.
	 * If anyone makes modifications to this class, this needs to be updated.
	 */
	public static final Date	LAST_MODIFIED	= new Date(1248256659000l);

	protected ImageCreatorToadlet(HighLevelSimpleClient client) {
		super(client);
	}

	public void handleMethodGET(URI uri, HTTPRequest req, ToadletContext ctx) throws ToadletContextClosedException, IOException, RedirectException {
		boolean needsGeneration = true;
		// If the browser has requested this image, then it will send this header
		if (ctx.getHeaders().containsKey("if-modified-since")) {
			try {
				// If the received date is equal to the last modification of this class, then it doesn't need regeneration
				if (ToadletContextImpl.parseHTTPDate(ctx.getHeaders().get("if-modified-since")).compareTo(LAST_MODIFIED) == 0) {
					// So we just send the NOT_MODIFIED response, and skip the generation
					ctx.sendReplyHeaders(304, "Not Modified", null, "image/png", 0, LAST_MODIFIED);
					needsGeneration = false;
				}
			} catch (ParseException pe) {
				// If something goes wrong, we regenerate
			}
		}
		if (needsGeneration) {
			// The text that will be drawn
			String text = req.getParam("text");
			
			// If width or height is specified, we use it, if not, then we use the default
			int requiredWidth = req.getParam("width").compareTo("") != 0 ? Integer.parseInt(req.getParam("width").endsWith("px")?req.getParam("width").substring(0, req.getParam("width").length()-2):req.getParam("width")) : DEFAULT_WIDTH;
			int requiredHeight = req.getParam("height").compareTo("") != 0 ? Integer.parseInt(req.getParam("height").endsWith("px")?req.getParam("height").substring(0, req.getParam("height").length()-2):req.getParam("height")) : DEFAULT_HEIGHT;
			
			// This is the image we are making
			Bitmap buffer  = Bitmap.createBitmap(requiredWidth, requiredHeight, Bitmap.Config.ARGB_4444);
			Canvas g = new Canvas(buffer);
			Paint paint = new Paint();
			paint.setAntiAlias(false);
		    paint.setStrokeWidth(1);
		    Typeface tf = Typeface.create(Typeface.DEFAULT,1);
		    paint.setTypeface(tf);
		    Rect bounds = new Rect();
			
			// We then specify the maximum font size that fits in the image
			// For this, we start at 1, and increase it, until it overflows. This-1 will be the font size
			float size = 1;
			paint.setTextSize(size);
			int width = 0;
			int height = 0;	
			while (width < requiredWidth && height < requiredHeight) {
				paint.getTextBounds(text, 0, text.length(), bounds);

				// calculate the size of the text
				width = (int) bounds.right-bounds.left;
				height = (int) bounds.top-bounds.bottom;
				Log.d("width",Integer.toString(width));
				paint.setTextSize(++size);
			}
			paint.setTextSize(size-2);
			paint.getTextBounds(text, 0, text.length(), bounds);
			width = (int) bounds.right-bounds.left;
			height = (int) bounds.top-bounds.bottom;
			
			// actually do the drawing

			paint.setColor(android.graphics.Color.BLACK);
			paint.setStyle(Style.FILL);
			g.drawRect(bounds.left, bounds.top, bounds.right, bounds.bottom, paint);
			
			paint.setColor(android.graphics.Color.WHITE);
			paint.setStyle(Style.FILL);

			// We position it to the center. Note that this is not the upper left corner
			//g2.drawString(text, (int) (requiredWidth / 2 - bounds.getWidth() / 2), (int) (requiredHeight / 2 + bounds.getHeight() / 4));
			g.drawText(text,(float) (requiredWidth / 2 - width / 2) ,(float) (requiredHeight / 2 - height/2), paint);
			// Write the data, and send the modification data to let the client cache it
			
			Bucket data = ctx.getBucketFactory().makeBucket(-1);
			OutputStream os = data.getOutputStream();

			try {
				buffer.compress(Bitmap.CompressFormat.PNG,90,os);
			} 
			catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//} finally {
				//os.close();
//			}
			MultiValueTable<String, String> headers=new MultiValueTable<String, String>();
			ctx.sendReplyHeaders(200, "OK", headers, "image/png", data.size(), LAST_MODIFIED);
			ctx.writeData(data);
		}
	}

	@Override
	public String path() {
		return "/imagecreator/";
	}

}
