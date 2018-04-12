package org.openmore.sourcegenerator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.openmore.common.utils.ResponseResult;
import org.openmore.sourcegenerator.src.DtoParam;
import org.openmore.sourcegenerator.src.FreeMakerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Created by LZ on 2017/6/20.
 */
@CrossOrigin(maxAge = 3600)
@Api(value = "/dto", tags = "DTO半自动生成器", description = "DTO半自动生成器")
@RequestMapping(value = "/dto", produces = {APPLICATION_JSON_UTF8_VALUE})
@Controller
public class DtoCreatorController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET, value = "/home")
    public String pageToDtoCreater() {
        return "index";
    }


    @ApiOperation(value = "DTO生成器返回生成内容", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = "请求失败：参数错误", response = ResponseResult.class)})
    @RequestMapping(method = RequestMethod.POST, value = "/source-viewer")
    @ResponseBody
    public ResponseEntity showSourceDo(@RequestParam(required = false) String t,
                                       @RequestParam(required = false) String packageName,
                                       @RequestParam(required = false) String className,
                                       @RequestParam(required = false) String className_zn,
                                       @RequestParam(required = false) String controller_desc,
                                       @RequestParam(required = false) String attrs) {
        List<DtoParam> att = null;
        try {
            Gson gson = new Gson();
            att = gson.fromJson(attrs,
                    new TypeToken<List<DtoParam>>() {
                    }.getType());
            if (null != att) {
                for (DtoParam ap : att) {
                    if (null == ap || null == ap.getName() || "".equals(ap.getName())) {
                        att.remove(ap);
                    }
                }
            }
            logger.debug(att == null ? "att is null" : att.size() + "");
        } catch (Exception e) {
            logger.debug("参数错误->无法解析");
        }

        try {
            Map<String, Object> root = new HashMap<String, Object>();
            root.put("basepackage", packageName);
            root.put("className", className);
            root.put("className_zn", className_zn);
            root.put("controller_desc", controller_desc);
            root.put("attrs", att);
            if (null == t) {
                return new ResponseEntity("参数错误->t is null", HttpStatus.BAD_REQUEST);
            }
            if (t.equals("dto")) {//生成dto
                root.put("subpackage", "dto");
                String modelFile1 = "${className}Dto.java";
                String data = FreeMakerFactory.getInstance().freeMaker(modelFile1, root);
                data = new ObjectMapper().writeValueAsString(data);
                return new ResponseEntity(data, HttpStatus.OK);
            } else if (t.equals("service")) {//生成service
                root.put("subpackage", "service");
                String modelFile1 = "${className}Service.java";
                String data = FreeMakerFactory.getInstance().freeMaker(modelFile1, root);
                data = new ObjectMapper().writeValueAsString(data);
                return new ResponseEntity(data, HttpStatus.OK);
            } else if (t.equals("serviceImpl")) {//生成serviceImpl
                root.put("subpackage", "service");
                String modelFile1 = "${className}ServiceImpl.java";
                String data = FreeMakerFactory.getInstance().freeMaker(modelFile1, root);
                data = new ObjectMapper().writeValueAsString(data);
                return new ResponseEntity(data, HttpStatus.OK);
            } else if (t.equals("controller")) {//生成controller
                root.put("subpackage", "controller");
                String modelFile1 = "${className}Controller.java";
                String data = FreeMakerFactory.getInstance().freeMaker(modelFile1, root);
                data = new ObjectMapper().writeValueAsString(data);
                return new ResponseEntity(data, HttpStatus.OK);
            } else {
                return new ResponseEntity("参数错误->t is invalid", HttpStatus.METHOD_NOT_ALLOWED);
            }
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @ApiOperation(value = "DTO生成器并生成文件", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = "请求失败：参数错误", response = ResponseResult.class)})
    @RequestMapping(method = RequestMethod.POST, value = "/source-generator")
    @ResponseBody
    public ResponseEntity generateSourceFile(@RequestParam(required = false) String packageName,
                                             @RequestParam(required = false) String className,
                                             @RequestParam(required = false) String className_zn,
                                             @RequestParam(required = false) String controller_desc,
                                             @RequestParam(required = false) String attrs) {

        logger.debug(packageName);
        logger.debug(className);
        logger.debug(className_zn);
        logger.debug(controller_desc);
        logger.debug(attrs);

        List<DtoParam> att = null;
        try {
            Gson gson = new Gson();
            att = gson.fromJson(attrs,
                    new TypeToken<List<DtoParam>>() {
                    }.getType());
            if (null != att) {
                for (DtoParam ap : att) {
                    if (null == ap || null == ap.getName() || "".equals(ap.getName())) {
                        att.remove(ap);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("参数错误->无法解析");
        }

        try {
            Map<String, Object> root = new HashMap<String, Object>();
            root.put("basepackage", packageName);
            root.put("className", className);
            root.put("className_zn", className_zn);
            root.put("controller_desc", controller_desc);
            root.put("attrs", att);

            // 文件生成路径
            String packageNameWithSeparator = packageName.replace(".", File.separator);
            String sourcePath = "src" + File.separator + "main" + File.separator + "java" + File.separator + packageNameWithSeparator;
            sourcePath = System.getProperty("user.dir") + File.separator + sourcePath;

            String testSourcePath = "src" + File.separator + "test" + File.separator + "java" + File.separator + packageNameWithSeparator;
            testSourcePath = System.getProperty("user.dir") + File.separator + testSourcePath;


            //生成dto
            root.put("subpackage", "dto");
            String templateFileName = "${className}Dto.java";
            String fileName = className + "Dto.java";
            String sourcePathName = sourcePath + File.separator + "dto";
            FreeMakerFactory.getInstance().freeMaker(templateFileName, sourcePathName, fileName, root);
            logger.debug(">> " + sourcePathName + File.separator + fileName);

            // 生成service
            root.put("subpackage", "service");
            templateFileName = "${className}Service.java";
            fileName = className + "Service.java";
            sourcePathName = sourcePath + File.separator + "service";
            FreeMakerFactory.getInstance().freeMaker(templateFileName, sourcePathName, fileName, root);
            logger.debug(">> " + sourcePathName + File.separator + fileName);

            //生成serviceImpl
            root.put("subpackage", "service");
            templateFileName = "${className}ServiceImpl.java";
            fileName = className + "ServiceImpl.java";
            sourcePathName = sourcePath + File.separator + "service" + File.separator + "impl";
            FreeMakerFactory.getInstance().freeMaker(templateFileName, sourcePathName, fileName, root);
            logger.debug(">> " + sourcePathName + File.separator + fileName);


            //生成controller
            root.put("subpackage", "controller");
            templateFileName = "${className}Controller.java";
            fileName = className + "Controller.java";
            sourcePathName = sourcePath + File.separator + "controller";
            FreeMakerFactory.getInstance().freeMaker(templateFileName, sourcePathName, fileName, root);
            logger.debug(">> " + sourcePathName + File.separator + fileName);

            // 生成Test
            root.put("subpackage", "service");
            templateFileName = "${className}ServiceTest.java";
            fileName = className + "ServiceTest.java";
            sourcePathName = testSourcePath + File.separator + "service";
            FreeMakerFactory.getInstance().freeMaker(templateFileName, sourcePathName, fileName, root);
            logger.debug(">> " + sourcePathName + File.separator + fileName);

            return new ResponseEntity("ok", HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
