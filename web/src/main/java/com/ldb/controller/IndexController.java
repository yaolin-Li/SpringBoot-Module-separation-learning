package com.ldb.controller;

import com.ldb.utils.ConfigStrUtil;
import com.ldb.controller.utils.RequestUtil;
import com.ldb.pojo.po.VisitorPO;
import com.ldb.pojo.vo.BlogVO;
import com.ldb.pojo.vo.CommentVO;
import com.ldb.service.BlogService;
import com.ldb.service.CommentService;
import com.ldb.service.LikeService;
import com.ldb.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by ldb on 2017/4/17.
 * 默认情况下Spring MVC将模型中的数据存储到request域中。当一个请求结束后，数据就失效了。
 * 如果要跨页面使用。那么需要使用到session。而@SessionAttributes注解就可以使得模型中的数据存储一份到session域中。
 */
@Controller
@SessionAttributes(value = {"readNum","likeCount","hotBlogList","newCommentList"})
public class IndexController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = {"/","/index"})
    public ModelAndView goIndex(HttpServletRequest request){
        ModelAndView mav=new ModelAndView("foreground/index");
        //获取点赞数量
        mav.addObject("likeCount",likeService.getLikeCount());
        //获取游客浏览器、操作系统、IP信息
        String userBrowser=RequestUtil.getUserBrowser(request);
        String userOS=RequestUtil.getUserOS(request);
        String userIP=RequestUtil.getUserIP(request);
        VisitorPO visitorPO=new VisitorPO(userIP,userBrowser,userOS);
        visitorService.addVisitor(visitorPO);

        //获取游客浏览数量
        Long readNum=visitorService.getReadNum();
        mav.addObject("readNum",readNum);

        //获取最新5篇博文
        List<BlogVO> newBlogList = blogService.listNewBlog();
        mav.addObject("newBlogList",newBlogList);

        //获取热门5篇博文
        List<BlogVO> hotBlogList = blogService.listHotBlog();
        mav.addObject("hotBlogList",hotBlogList);

        List<CommentVO> newCommentList = commentService.listNewComment();
        mav.addObject("newCommentList",newCommentList);
        return mav;
    }

    @RequestMapping(value="/like",method = RequestMethod.POST)
    @ResponseBody
    public String clickLike(HttpServletRequest request){
        String ip=RequestUtil.getUserIP(request);
        int resultNum=likeService.addLike(ip);
        if(resultNum>0){
            // session.removeAttribute("readNum");
            // session.setAttribute("readNum",visitorService.getReadNum());
            return ConfigStrUtil.SUCCESS;
        }else{
            return ConfigStrUtil.ERROR;
        }

    }
}
