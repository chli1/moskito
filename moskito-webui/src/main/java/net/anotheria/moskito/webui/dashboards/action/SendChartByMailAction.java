package net.anotheria.moskito.webui.dashboards.action;

import net.anotheria.maf.action.AbortExecutionException;
import net.anotheria.maf.action.ActionMapping;
import net.anotheria.maf.bean.FormBean;
import net.anotheria.maf.json.JSONResponse;
import net.anotheria.moskito.core.plugins.chart.ChartSenderHolder;
import net.anotheria.moskito.webui.shared.action.BaseAJAXMoskitoUIAction;
import net.anotheria.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class SendChartByMailAction extends BaseAJAXMoskitoUIAction {

	/**
	 * Request param: image as bytes array
	 */
	private static final String PARAM_IMAGE = "pImage";

	/**
	 * Attribute name: indication if menu navigation is collapsed or not
	 */
	private static final String PARAM_FILENAME = "pName";

	/**
	 * Attribute name: indication if menu navigation is collapsed or not
	 */
	private static final String PARAM_EMAILS = "pEmails";

	/**
	 * Email patter
	 */
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Override
	protected void invokeExecute(ActionMapping mapping, FormBean bean, HttpServletRequest req, HttpServletResponse res, JSONResponse jsonResponse) throws AbortExecutionException, IOException, JSONException {
		String image64 = req.getParameter(PARAM_IMAGE);
		String fileName = req.getParameter(PARAM_FILENAME);
		String emailsStr = req.getParameter(PARAM_EMAILS);
		JSONObject jsonObject = new JSONObject();

		if(StringUtils.isEmpty(emailsStr)) {
			jsonObject.put("message", "Please enter recipient email");
			jsonResponse.setData(jsonObject);
			return;
		}

		String[] emails = emailsStr.split(",");
		for (String email : emails) {
			if(!isEmailValid(email.trim())) {
				jsonObject.put("message", "Wrong email: "+email);
				jsonResponse.setData(jsonObject);
				return;
			}
		}

		if (!ChartSenderHolder.INSTANCE.isChartSenderRegistered()) {
			jsonObject.put("message", "Chart sender not registered. Please switch on needed plugin.");
			jsonResponse.setData(jsonObject);
			return;
		}

		image64 = image64.substring(image64.indexOf(",") + 1);
		ChartSenderHolder.INSTANCE.getChartSender().sendChart(fileName, image64, "Chart " + fileName, emails);

		jsonObject.put("message", "Chart was successfully sent");
		jsonResponse.setData(jsonObject);
	}

	private boolean isEmailValid(String email) {
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
