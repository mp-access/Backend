package ch.uzh.ifi.access.course.config;

import ch.uzh.ifi.access.course.Model.security.GrantedCourseAccess;
import org.junit.Assert;
import org.junit.Test;

public class JwtAccessTokenCustomizerTest {

    @Test
    public void parseCourseAccessStudent() {
        JwtAccessTokenCustomizer tokenCustomizer = new JwtAccessTokenCustomizer(null);

        GrantedCourseAccess grantedCourseAccess = tokenCustomizer.parseCourseAccess("/Informatics 1/students");

        Assert.assertEquals("Informatics 1", grantedCourseAccess.getCourseKey());
        Assert.assertTrue(grantedCourseAccess.isStudent());
        Assert.assertFalse(grantedCourseAccess.isAuthor());
    }

    @Test
    public void parseCourseAccessAuthor() {
        JwtAccessTokenCustomizer tokenCustomizer = new JwtAccessTokenCustomizer(null);

        GrantedCourseAccess grantedCourseAccess = tokenCustomizer.parseCourseAccess("/Informatics 1/authors");

        Assert.assertEquals("Informatics 1", grantedCourseAccess.getCourseKey());
        Assert.assertFalse(grantedCourseAccess.isStudent());
        Assert.assertTrue(grantedCourseAccess.isAuthor());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseCourseAccessMissingSubgroup() {
        JwtAccessTokenCustomizer tokenCustomizer = new JwtAccessTokenCustomizer(null);

        GrantedCourseAccess grantedCourseAccess = tokenCustomizer.parseCourseAccess("/Informatics 1/");

        Assert.assertEquals("Informatics 1", grantedCourseAccess.getCourseKey());
        Assert.assertFalse(grantedCourseAccess.isStudent());
        Assert.assertTrue(grantedCourseAccess.isAuthor());
    }

    @Test
    public void parseCourseAccessMissingEmptyStringAndNull() {
        JwtAccessTokenCustomizer tokenCustomizer = new JwtAccessTokenCustomizer(null);

        GrantedCourseAccess grantedCourseAccess = tokenCustomizer.parseCourseAccess("");
        Assert.assertEquals(grantedCourseAccess, GrantedCourseAccess.empty());

        grantedCourseAccess = tokenCustomizer.parseCourseAccess(null);
        Assert.assertEquals(grantedCourseAccess, GrantedCourseAccess.empty());
    }
}