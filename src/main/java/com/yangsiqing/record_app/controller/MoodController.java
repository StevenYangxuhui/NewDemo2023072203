package com.yangsiqing.record_app.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yangsiqing.record_app.model.MoodRecord;
import com.yangsiqing.record_app.service.MoodRecordService;
import com.yangsiqing.record_app.util.SubmitterResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MoodController {

    @Autowired
    private MoodRecordService moodRecordService;

    @Value("${mood.hosts.nfy:}")
    private String hostsNfyCsv;

    @Value("${mood.hosts.ysq:localhost,127.0.0.1}")
    private String hostsYsqCsv;

    /** 0. 主页 */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /** 1. 表单页：按请求域名解析 submitter，直接展示表单 */
    @GetMapping("/mood")
    public String showForm(HttpServletRequest request, Model model) {
        String submitter = SubmitterResolver.resolve(
                request,
                SubmitterResolver.parseHosts(hostsNfyCsv),
                SubmitterResolver.parseHosts(hostsYsqCsv)
        );
        model.addAttribute("submitter", submitter);
        return "mood";
    }

    /** 2. 提交表单：按请求域名解析 submitter，入库后重定向成功页 */
    @PostMapping("/mood/submit")
    public String submit(
            HttpServletRequest request,
            @RequestParam(required = false) String happenThing,
            @RequestParam(required = false) String mood,
            @RequestParam(required = false) String partnerImage) {
        String submitter = SubmitterResolver.resolve(
                request,
                SubmitterResolver.parseHosts(hostsNfyCsv),
                SubmitterResolver.parseHosts(hostsYsqCsv)
        );
        MoodRecord record = new MoodRecord(happenThing, mood, partnerImage, submitter);
        moodRecordService.save(record);
        return "redirect:/mood/success";
    }

    /** 3. 提交成功页 */
    @GetMapping("/mood/success")
    public String success() {
        return "success";
    }

    /** 4. 查看记录列表：按域名过滤，只显示当前用户的记录 */
    @GetMapping("/mood/list")
    public String list(HttpServletRequest request, Model model) {
        String submitter = SubmitterResolver.resolve(
                request,
                SubmitterResolver.parseHosts(hostsNfyCsv),
                SubmitterResolver.parseHosts(hostsYsqCsv)
        );
        QueryWrapper<MoodRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("submitter", submitter);
        queryWrapper.orderByDesc("create_time");
        List<MoodRecord> records = moodRecordService.list(queryWrapper);
        model.addAttribute("records", records);
        return "list";
    }

    /** 5. 删除记录：只能删除自己的记录 */
    @GetMapping("/mood/delete")
    public String delete(HttpServletRequest request, @RequestParam Long id) {
        String submitter = SubmitterResolver.resolve(
                request,
                SubmitterResolver.parseHosts(hostsNfyCsv),
                SubmitterResolver.parseHosts(hostsYsqCsv)
        );
        MoodRecord record = moodRecordService.getById(id);
        if (record != null && submitter.equals(record.getSubmitter())) {
            moodRecordService.removeById(id);
        }
        return "redirect:/mood/list";
    }
}