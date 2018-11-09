import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.JButton;
import javax.swing.JTextArea;

public class GuiReader {

	private JFrame frame;
	private JTextField typeText;
	private JTextField categoryText;
	public JSONArray jsnArr;

	public JSONArray getJsnArr() {
		return jsnArr;
	}

	public void setJsnArr(JSONArray jsnArr) {
		this.jsnArr = jsnArr;
	}
	
	public JSONObject makeHttpRequest(String url,String method,List<NameValuePair>params) {
		InputStream is = null;
		String json = "";
		JSONObject jObj = null;
		
		try {
			if(method == "POST") {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}else if(method == "GET") {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);
				
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			
			while ((line = reader.readLine())!=null) {
				sb.append(line +  "\n");
			}
			is.close();
			json = sb.toString();
			jObj = new JSONObject(json);
		}catch (JSONException e) {
			try {
				JSONArray jsnArr = new JSONArray(json);
				jObj = jsnArr.getJSONObject(0);
				setJsnArr(jsnArr);
			}catch(JSONException ee) {
				ee.printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return jObj;
		
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiReader window = new GuiReader();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GuiReader() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblType = new JLabel("Type :");
		lblType.setBounds(10, 23, 60, 14);
		frame.getContentPane().add(lblType);
		
		typeText = new JTextField();
		typeText.setBounds(94, 20, 125, 20);
		frame.getContentPane().add(typeText);
		typeText.setColumns(10);
		
		JLabel lblCategory = new JLabel("Category :");
		lblCategory.setBounds(10, 61, 73, 14);
		frame.getContentPane().add(lblCategory);
		
		categoryText = new JTextField();
		categoryText.setBounds(94, 58, 125, 20);
		frame.getContentPane().add(categoryText);
		categoryText.setColumns(10);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(10, 86, 414, 164);
		frame.getContentPane().add(textArea);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(291, 46, 89, 23);
		frame.getContentPane().add(btnSearch);
		btnSearch.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						Thread thread1=new Thread(new Runnable()
								{

									@Override
									public void run() {
										// TODO Auto-generated method stub
										List<NameValuePair> params=new ArrayList<NameValuePair> ();
										params.add(new BasicNameValuePair("product_type", typeText.getText()));
										params.add(new BasicNameValuePair("category", categoryText.getText()));
										String strurl = "http://makeup-api.herokuapp.com/api/v1/products.json";
										JSONObject jobj=makeHttpRequest(strurl,"GET",params);
										jsnArr=getJsnArr();
										
										try {
											if(jsnArr.length()<0)
											{
												textArea.setText("Result Not Found!");
											}
											else
											{
												StringBuilder stringbuilder=new StringBuilder();
												for(int i=0;i<jsnArr.length();i++)
												{
													JSONObject result=jsnArr.getJSONObject(i);
													if(result.optString("category").equalsIgnoreCase(categoryText.getText()))
													{
													String type=result.optString("product_type");
													String category=result.optString("category");
													String brand=result.optString("brand");
													
													String strSetText = "Product Type :"+type +" || Product Category :"+category+" || Product  Brand :"+brand;
													
													stringbuilder.append(strSetText+" \n");
													}
												}
												
												textArea.setText(stringbuilder.toString());
											}
											
										} catch (Exception e2) {
											// TODO: handle exception
											e2.printStackTrace();
										}
										
									}
							
								});thread1.start();
					}
			
				});
		
	
	}
}
