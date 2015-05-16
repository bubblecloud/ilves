package org.bubblecloud.ilves.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by tlaukkan on 5/14/2015.
 */
public class OAuthServiceTest {

    @Test
    @Ignore
    public void testGet() throws Exception {
        final String response = OAuthService.get("https://api.github.com/user/emails", "xxx");
        ObjectMapper objectMapper = new ObjectMapper();
        final ArrayList<Map<String, Object>> emailList = objectMapper.readValue(response, ArrayList.class);
        System.out.println(emailList);
        Assert.assertEquals(1, emailList.size());
        Assert.assertEquals("tommi.s.e.laukkanen@gmail.com", emailList.get(0).get("email"));
        Assert.assertTrue((Boolean) emailList.get(0).get("primary"));
        Assert.assertTrue((Boolean) emailList.get(0).get("verified"));
    }

    @Test
    @Ignore
    public void getGetEmail() throws Exception {
        final String response = OAuthService.getEmail("xxx");
        Assert.assertEquals("tommi.s.e.laukkanen@gmail.com", response);
    }
}
