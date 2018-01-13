package co.com.colciencias.hook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.events.Action;

public class LoginAction extends Action {
    public void run(HttpServletRequest req, HttpServletResponse res) {
        System.out.println("## My custom login action");
    }
}
