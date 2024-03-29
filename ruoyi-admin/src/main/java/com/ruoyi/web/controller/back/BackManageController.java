package com.ruoyi.web.controller.back;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.ruoyi.UserInfoUtil;
import com.ruoyi.UserOrderUtil;
import com.ruoyi.RunOrderUtil;
import com.ruoyi.alipay.domain.*;
import com.ruoyi.alipay.domain.util.WitAppExport;
import com.ruoyi.alipay.service.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.constant.StaticConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.StatisticsEntity;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.enums.WithdrawalStatusEnum;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.bean.WithdrawalBean;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.framework.util.AuditLogUtils;
import com.ruoyi.framework.util.DictionaryUtils;
import com.ruoyi.framework.util.GoogleUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysDictData;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.controller.tool.PropertyValidateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequestMapping("/back/merchant/admin")
@Controller
public class BackManageController extends BaseController {
    private final String prefix = "merchant/info";
    @Autowired
    private IMerchantInfoEntityService merchantInfoEntityService;
    @Autowired
    private IAlipayDealOrderAppService alipayDealOrderAppService;
    @Autowired
    private IAlipayRunOrderEntityService alipayRunOrderEntityService;
    @Autowired
    private IAlipayWithdrawEntityService alipayWithdrawEntityService;
    @Autowired
    private IAlipayUserRateEntityService alipayUserRateEntityService;
    @Autowired
    private IAlipayUserFundEntityService alipayUserFundEntityService;
    @Autowired
    private IAlipayChanelFeeService alipayChanelFeeService;
    @Autowired
    private SysPasswordService passwordService;
    @Autowired
    private DictionaryUtils dictionaryUtils;
    @Autowired
    private IAlipayBankListEntityService alipayBankListEntityService;
    @Autowired
    private GoogleUtils googleUtils;
    @Autowired
    private ISysDictDataService dictDataService;
    @Autowired
    private IAlipayProductService alipayProductService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 商户后台用户登陆显示详细信息
     */
    @GetMapping("/view")
    public String detail(ModelMap mmap) {
        SysUser sysUser = ShiroUtils.getSysUser();
        AlipayUserInfo userInfo = merchantInfoEntityService.selectBackUserByUserId(sysUser.getMerchantId());
        if (userInfo == null) {
            throw new BusinessException("此商户不存在");
        }
        UserInfo userWit = UserInfoUtil.selectUserInfoByName(userInfo.getUserId());
        if (ObjectUtil.isNotNull(userWit)) {
            userInfo.setWitAccount(userWit.getAmount().toString());
        }
        mmap.put("userInfo", userInfo);


        return prefix + "/detail";
    }


    @Autowired
    IAlipayProductService iAlipayProductService;

    //商户查询交易订单
    @GetMapping("/order/view")
    public String orderShow(ModelMap modelMap) {
        SysUser sysUser = ShiroUtils.getSysUser();
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        //查询产品类型下拉菜单
        //  List<AlipayProductEntity> list = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        List<AlipayProductEntity> list = alipayChanelFeeService.findProductByName(sysUser.getMerchantId());
        modelMap.put("productList", list);
        return prefix + "/order";
    }

    /**
     * 查询商户订单
     */
    @PostMapping("/order/list")
    @ResponseBody
    public TableDataInfo orderList(AlipayDealOrderApp alipayDealOrderApp) {
        SysUser sysUser = ShiroUtils.getSysUser();
        alipayDealOrderApp.setOrderAccount(sysUser.getMerchantId());
        startPage();
        List<AlipayDealOrderApp> list = alipayDealOrderAppService.selectAlipayDealOrderAppList(alipayDealOrderApp);

        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        List<AlipayProductEntity> productlist = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        ConcurrentHashMap<String, AlipayProductEntity> prCollect = productlist.stream().collect(Collectors.toConcurrentMap(AlipayProductEntity::getProductId, Function.identity(), (o1, o2) -> o1, ConcurrentHashMap::new));
        for (AlipayDealOrderApp order : list) {
            AlipayProductEntity product = prCollect.get(order.getRetain1());
            if (ObjectUtil.isNotNull(product)) {
                order.setRetain1(product.getProductName());
            }
        }
        return getDataTable(list);
    }


    /**
     * 商户订单导出
     */
    @PostMapping("/order/export")
    @ResponseBody
    public AjaxResult exportOrderApp(AlipayDealOrderApp alipayDealOrderApp) {
        SysUser sysUser = ShiroUtils.getSysUser();
        alipayDealOrderApp.setOrderAccount(sysUser.getMerchantId());
        startPage();
        List<AlipayDealOrderApp> list = alipayDealOrderAppService.selectAlipayDealOrderAppList(alipayDealOrderApp);
        for (AlipayDealOrderApp orderApp : list) {
            orderApp.setFeeId(null);
        }
        ExcelUtil<AlipayDealOrderApp> util = new ExcelUtil<AlipayDealOrderApp>(AlipayDealOrderApp.class);
        return util.exportExcel(list, "orderApp");
    }


    //商户查询交易流水
    @GetMapping("/running/view")
    public String runningShow() {
        return prefix + "/running";
    }

    @GetMapping("/runningWIt/view")
    public String runningWit() {
        return prefix + "/runningWit";
    }

    /**
     * 查询商户的交易流水
     */
    @PostMapping("/myRunwit")
    @ResponseBody
    public TableDataInfo myRunwit(RunOrder runOrder) {
        SysUser sysUser = ShiroUtils.getSysUser();
        runOrder.setAccount(sysUser.getMerchantId());
        UserInfo userWit = UserInfoUtil.selectUserInfoByName(sysUser.getMerchantId());
        if (ObjectUtil.isNull(userWit)) {
            ResPage<RunOrder> list = new ResPage<>();
            return getDataTablePage(list);
        }
        ReqPage<RunOrder> page = new ReqPage<RunOrder>();
        page.setData(runOrder);
        ResPage<RunOrder> list = RunOrderUtil.selectRunOrderListPage(page);
        return getDataTablePage(list);
    }

    @PostMapping("/running/list")
    @ResponseBody
    public TableDataInfo list(AlipayRunOrderEntity alipayRunOrderEntity) {
        SysUser sysUser = ShiroUtils.getSysUser();
        alipayRunOrderEntity.setOrderAccount(sysUser.getMerchantId());
        startPage();
        List<AlipayRunOrderEntity> list = alipayRunOrderEntityService.selectAlipayRunOrderEntityList(alipayRunOrderEntity);
        return getDataTable(list);
    }

    /**
     * 导出流水订单记录列表
     */
    @Log(title = "商户资金流水导出", businessType = BusinessType.EXPORT)
    @PostMapping("/running/export")
    @ResponseBody
    public AjaxResult export(AlipayRunOrderEntity alipayRunOrderEntity) {
        SysUser sysUser = ShiroUtils.getSysUser();
        alipayRunOrderEntity.setOrderAccount(sysUser.getMerchantId());
        startPage();
        List<AlipayRunOrderEntity> list = alipayRunOrderEntityService
                .selectAlipayRunOrderEntityList(alipayRunOrderEntity);
        for (AlipayRunOrderEntity runorder : list) {
            runorder.setAccountW(null);
            runorder.setAcountR(null);
        }
        ExcelUtil<AlipayRunOrderEntity> util = new ExcelUtil<AlipayRunOrderEntity>(AlipayRunOrderEntity.class);
        return util.exportExcel(list, "running");
    }

    @Log(title = "商户资金流水导出", businessType = BusinessType.EXPORT)
    @PostMapping("/myRunwit/export")
    @ResponseBody
    public AjaxResult myRunwiteEport(RunOrder runOrder) {
        SysUser sysUser = ShiroUtils.getSysUser();
        runOrder.setAccount(sysUser.getMerchantId());
        List<RunOrder> list =   RunOrderUtil .selectRunOrderEntityList(runOrder);
        ExcelUtil<RunOrder> util = new ExcelUtil<RunOrder>(RunOrder.class);
        return util.exportExcel(list, "running");
    }

    private String MARK = "_";

    //商户提现申请
    @GetMapping("/withdrawal/view")
    public String withdrawalShow(ModelMap modelMap) {
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setProductId("RECHARGE");
        List<AlipayProductEntity> list = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        if (CollUtil.isNotEmpty(list)) {
            AlipayProductEntity first = CollUtil.getFirst(list);
            modelMap.put("isLocation", first.getStatus());
        }
        return prefix + "/withdrawal";
    }

    /**
     * 查询商户提现记录列表
     */
  /*  @PostMapping("/withdrawal/list")
    @ResponseBody
    public TableDataInfo list(AlipayWithdrawEntity alipayWithdrawEntity) {
        SysUser sysUser = ShiroUtils.getSysUser();
        alipayWithdrawEntity.setUserId(sysUser.getMerchantId());
        startPage();
        List<AlipayWithdrawEntity> list = alipayWithdrawEntityService.selectAlipayWithdrawEntityList(alipayWithdrawEntity);
        return getDataTable(list);
    }*/
    @PostMapping("/withdrawal/list")
    @ResponseBody
    public TableDataInfo list(UserOrder userOrder) {
        ReqPage<UserOrder> page = new ReqPage<UserOrder>();
        page.setData(userOrder);
        SysUser sysUser = ShiroUtils.getSysUser();
        userOrder.setName(sysUser.getMerchantId());
        UserInfo userWit = UserInfoUtil.selectUserInfoByName(sysUser.getMerchantId());
        if (ObjectUtil.isNull(userWit)) {
            ResPage<RunOrder> list = new ResPage<>();
            return getDataTablePage(list);
        }
        ResPage<UserOrder> list = UserOrderUtil.selectProductListPage(page);
        return getDataTablePage(list);
    }

    protected TableDataInfo getDataTablePage(ResPage list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(list.getRows());
        rspData.setTotal(list.getTotal());
        return rspData;
    }

    /**
     * 导出流水订单记录列表
     */
    @Log(title = "商户代付详情导出", businessType = BusinessType.EXPORT)
    @PostMapping("/withdrawal/export")
    @ResponseBody
    public AjaxResult withdrawalExport(UserOrder alipayWithdrawEntity) {
        SysUser sysUser = ShiroUtils.getSysUser();
        alipayWithdrawEntity.setName(sysUser.getMerchantId());
        List<UserOrder> userOrders = UserOrderUtil.selectUserOrderList(alipayWithdrawEntity);
        ExcelUtil<UserOrder> util = new ExcelUtil<UserOrder>(UserOrder.class);
        return util.exportExcel(userOrders, "wit");
    }


    /**
     * 商户发起申请提现
     */
    @GetMapping("/withdrawal/apply")
    public String apply(ModelMap mmap) {
        SysUser sysUser = ShiroUtils.getSysUser();
        AlipayUserFundEntity alipayUserFundEntity = alipayUserFundEntityService.findAlipayUserFundByUserId(sysUser.getMerchantId());
        AlipayBankListEntity alipayBankListEntity = new AlipayBankListEntity();
        alipayBankListEntity.setAccount(sysUser.getMerchantId());
        List<AlipayBankListEntity> list = alipayBankListEntityService.selectAlipayBankListEntityList(alipayBankListEntity);
        mmap.put("bankList", list);
        mmap.put("userFund", alipayUserFundEntity);
        SysDictData dictData = new SysDictData();
        dictData.setDictType("system_bankcode");
        List<SysDictData> bankcode = dictDataService.selectDictDataList(dictData);
        mmap.put("bankcode", bankcode);
        return prefix + "/apply";
    }

    /**
     * 保存提现提案
     */
    @Log(title = "提现申请", businessType = BusinessType.INSERT)
    @PostMapping("/withdrawal/save")
    @ResponseBody
    @RepeatSubmit
    public AjaxResult witSave(AlipayWithdrawEntity alipayWithdrawEntity) {
        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        String payPassword = (String) alipayWithdrawEntity.getParams().get("payPassword");
        String verify = passwordService.encryptPassword(currentUser.getLoginName(), payPassword, currentUser.getSalt());
        if (!currentUser.getFundPassword().equals(verify)) {
            return AjaxResult.error("密码验证失败");
        }
        SysDictData dictData = new SysDictData();
        dictData.setDictType("system_bankcode");
        List<SysDictData> bankcode = dictDataService.selectDictDataList(dictData);
        ConcurrentHashMap<String, SysDictData> bankcodeCollect = bankcode.stream().collect(Collectors.toConcurrentMap(SysDictData::getDictValue, Function.identity(), (o1, o2) -> o1, ConcurrentHashMap::new));
        SysDictData sysDictData = bankcodeCollect.get(alipayWithdrawEntity.getBankcode());
        //正式环境解注
        //验证谷歌验证码
        String googleCode = alipayWithdrawEntity.getParams().get("googleCode").toString();
        int is = googleUtils.verifyGoogleCode(currentUser.getLoginName(), googleCode);
        if (is == 0) {
            return AjaxResult.error("未绑定谷歌验证器");
        } else if (is - 1 > 0) {
            return AjaxResult.error("谷歌验证码验证失败");
        }
        //获取alipay处理接口URL
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);
        String urlPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_SERVICE_API_KEY, StaticConstants.ALIPAY_SERVICE_API_VALUE_6);
        AlipayUserInfo alipayUserInfo = merchantInfoEntityService.selectBackUserByUserId(currentUser.getMerchantId());
        Map<String, Object> mapParam = Collections.synchronizedMap(Maps.newHashMap());
        mapParam.put("appid", currentUser.getMerchantId());
        mapParam.put("ordertime", new Date());
        mapParam.put("amount", alipayWithdrawEntity.getAmount());
        mapParam.put("acctno", alipayWithdrawEntity.getBankNo());
        mapParam.put("acctname", alipayWithdrawEntity.getAccname());
        mapParam.put("apply", currentUser.getLoginName());
        mapParam.put("mobile", alipayWithdrawEntity.getMobile());
        mapParam.put("bankcode", alipayWithdrawEntity.getBankcode());//后台代付
        mapParam.put("bankName", sysDictData.getDictLabel());//后台代付
//        mapParam.put("bankcode","ICBC");//后台代付
//        mapParam.put("bankName",alipayWithdrawEntity.getBankName());//后台代付
        mapParam.put("dpaytype", "Bankcard");//银行卡代付类型
        mapParam.put("orderStatus", WithdrawalStatusEnum.WITHDRAWAL_STATUS_PROCESS.getCode());
        mapParam.put("notifyurl", "http://localhost/iiiii");
        mapParam.put("apporderid", GenerateOrderNo.getInstance().Generate(StaticConstants.MERCHANT_WITHDRAWAL));
        mapParam.put("sign", HashKit.md5(MapDataUtil.createParam(mapParam) + alipayUserInfo.getPayPasword()));
        Map<String, String> extraParam = Maps.newHashMap();
        extraParam.put("userId", currentUser.getMerchantId());
        extraParam.put("publicKey", alipayUserInfo.getPublicKey());
        extraParam.put("manage", "manage");
        return HttpUtils.adminMap2Gateway(mapParam, ipPort + "/deal/wit", extraParam);
    }

    private String RATE_KEY = "usdtrate" + MARK;
    private Map cache = new ConcurrentHashMap();

    /**
     * 商户保存修改信息
     *
     * @param alipayUserInfo
     * @return
     */
    @Log(title = "商户信息", businessType = BusinessType.UPDATE)
    @PostMapping("/audit")
    @ResponseBody
    public AjaxResult toSave(AlipayUserInfo alipayUserInfo) {
        return toAjax(merchantInfoEntityService.updateMerchantByBackAdmin(alipayUserInfo));
    }

    @Log(title = "内充申请-不走自动回调", businessType = BusinessType.INSERT)
    @PostMapping("/withdrawal/rechargeLocation")
    @ResponseBody
    @RepeatSubmit
    public AjaxResult rechargeLocation(AlipayWithdrawEntity alipayWithdrawEntity) {
        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        String payPassword = (String) alipayWithdrawEntity.getParams().get("payPassword");
        String verify = passwordService.encryptPassword(currentUser.getLoginName(), payPassword, currentUser.getSalt());
        if (!currentUser.getFundPassword().equals(verify)) {
            return AjaxResult.error("密码验证失败");
        }
        String userId = currentUser.getMerchantId();
        AlipayUserRateEntity rateEntity = alipayUserRateEntityService.findRateByType(userId, "RECHARGE");
        AlipayUserInfo userInfo = new AlipayUserInfo();
        userInfo.setUserId(userId);
        List<AlipayUserInfo> alipayUserInfos = merchantInfoEntityService.selectMerchantInfoEntityList(userInfo);
        SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
        String userid = rateEntity.getUserId();
        String key = alipayUserInfos.get(0).getPayPasword();//交易密钥
        String publicKey = alipayUserInfos.get(0).getPublicKey();
        String amount = alipayWithdrawEntity.getAmount().toString();
        String orderId = GenerateOrderNo.getInstance().Generate("USDT");
        String post = postWit(amount, userid, rateEntity.getPayTypr(), orderId, key, publicKey);
        return AjaxResult.success();
    }

    /**
     * 商户发起申请提现
     */
    @GetMapping("/withdrawal/applyBatch")
    public String applyBatch(ModelMap mmap) {
        /*SysUser sysUser = ShiroUtils.getSysUser();
        AlipayUserFundEntity alipayUserFundEntity = alipayUserFundEntityService.findAlipayUserFundByUserId(sysUser.getMerchantId());
        AlipayBankListEntity alipayBankListEntity = new AlipayBankListEntity();
        alipayBankListEntity.setAccount(sysUser.getMerchantId());
        List<AlipayBankListEntity> list = alipayBankListEntityService.selectAlipayBankListEntityList(alipayBankListEntity);
        mmap.put("bankList", list);
        mmap.put("userFund", alipayUserFundEntity);
        SysDictData dictData = new SysDictData();
        dictData.setDictType("system_bankcode");
        List<SysDictData> bankcode = dictDataService.selectDictDataList(dictData);
        mmap.put("bankcode", bankcode);*/
        SysUser currentUser = ShiroUtils.getSysUser();
        AlipayUserFundEntity alipayUserFundEntity = alipayUserFundEntityService.findAlipayUserFundByUserId(currentUser.getMerchantId());
        mmap.put("userFund", alipayUserFundEntity);
        return prefix + "/applyBatch";
    }

    //验证资金密码和谷歌动态口令
    @PostMapping("/verifyGoogleCode")
    @ResponseBody
    public AjaxResult verify(String fundPassword, String googleCode, String vType) {
        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        int i = 1;
        if ("all".equals(vType) || "fund".equals(vType)) {
            String verify = passwordService.encryptPassword(currentUser.getLoginName(), fundPassword, currentUser.getSalt());
            if (!currentUser.getFundPassword().equals(verify)) {
                return AjaxResult.error("资金管理密码验证失败");
            }
            i = i + 1;
        }
        if ("all".equals(vType) || "google".equals(vType)) {
            //验证谷歌验证码
            int is = googleUtils.verifyGoogleCode(currentUser.getLoginName(), googleCode);
            if (is == 0) {
                return AjaxResult.error("未绑定谷歌验证器，请联系管理员");
            } else if (is - 1 > 0) {
                return AjaxResult.error("谷歌动态口令验证失败");
            }
            i = i + 1;
        }
        if (i > 0) {
            return AjaxResult.success();
        } else {
            return AjaxResult.error("验证失败");
        }
    }

    private AjaxResult batchWithdrawal(List<WithdrawalBean> listBean) {
        AjaxResult result = null;
        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        String userId = currentUser.getLoginName();
        try {
            // 获取当前线程的方法名
            String threadName = Thread.currentThread().getStackTrace()[1].getMethodName();
            // getMethod里面的参数对应updateUserInfo方法的参数，固定形式的，不可少
            Method method = BackManageController.class.getMethod(threadName, List.class);
            StringBuffer change = new StringBuffer();
            change.append("进入提现申请处理方法时间：" + new Date().getTime()).append("\n");
            int j = 1;
            for (WithdrawalBean item : listBean) {
                Map<String, Object> logMap = Collections.synchronizedMap(Maps.newHashMap());
                logMap.put("appid", currentUser.getMerchantId());
                logMap.put("amount", item.getAmount());
                logMap.put("acctno", item.getBankNo());
                logMap.put("acctname", item.getAccname());
                logMap.put("bankName", item.getBankName());
                logMap.put("apply", currentUser.getLoginName());
                change.append("第" + j + "笔参数").append(logMap.toString()).append("\n");
                j++;
            }
            // 调用修改方法
            AuditLogUtils.alterAnnotationOn(method, change.toString());
        } catch (Exception e) {
            logger.info("动态修改日志错误");
        }
        //判断是否有此key
        boolean isExist = redisUtil.hasKey(StaticConstants.MERCHANT_WITHDRAWAL_PARAMS_KEY + userId);
        if (!isExist) {
            redisUtil.set(StaticConstants.MERCHANT_WITHDRAWAL_PARAMS_KEY + userId, listBean, 300);
        } else {
            List<WithdrawalBean> redisObject = (List<WithdrawalBean>) redisUtil.get(StaticConstants.MERCHANT_WITHDRAWAL_PARAMS_KEY + userId);
            //对list内容进行比较判断
            boolean isSame = CommonUtils.compareToList(redisObject, listBean);
            if (isSame) {
                return AjaxResult.error("5分钟之内不允许连续提交完全相同的下发记录");
            } else {
                redisUtil.set(StaticConstants.MERCHANT_WITHDRAWAL_PARAMS_KEY + userId, listBean, 300);
            }
        }

        if (listBean.size() > 10) {
            throw new BusinessException("单次最多提交10笔代付订单");
        }
        //获取alipay处理接口URL
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);
        String urlPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_SERVICE_API_KEY, StaticConstants.ALIPAY_SERVICE_API_VALUE_6);
        AlipayUserInfo alipayUserInfo = merchantInfoEntityService.selectBackUserByUserId(currentUser.getMerchantId());
        int successCount = 0;
        int failCount = 0;
        for (WithdrawalBean item : listBean) {
            Map<String, Object> mapParam = Collections.synchronizedMap(Maps.newHashMap());
            String[] str = item.getBankName().split(",");
            mapParam.put("appid", currentUser.getMerchantId());
            mapParam.put("ordertime", new Date());
            mapParam.put("amount", item.getAmount());
            mapParam.put("acctno", item.getBankNo());
            mapParam.put("acctname", item.getAccname());
            mapParam.put("apply", currentUser.getLoginName());
            if (str.length > 1) {
                mapParam.put("bankName", str[1]);
            } else {
                mapParam.put("bankName", str[0]);
            }
            mapParam.put("bankcode", "ICBC");
            mapParam.put("orderStatus", WithdrawalStatusEnum.WITHDRAWAL_STATUS_PROCESS.getCode());
            mapParam.put("notifyurl", "http://127.0.0.1/");
            mapParam.put("apporderid", GenerateOrderNo.getInstance().Generate(StaticConstants.MERCHANT_WITHDRAWAL));
            logger.info("验签参数：{}", MapDataUtil.createParam(mapParam));
            mapParam.put("sign", HashKit.md5(MapDataUtil.createParam(mapParam) + alipayUserInfo.getPayPasword()));
            Map<String, String> extraParam = Maps.newHashMap();
            extraParam.put("userId", currentUser.getMerchantId());
            extraParam.put("publicKey", alipayUserInfo.getPublicKey());
            extraParam.put("manage", "manage");
            extraParam.put("flag", "true");
            result = HttpUtils.adminMap2Gateway(mapParam, ipPort + "/deal/wit", extraParam);
            logger.info("adminMap2Gateway返回的结果：{}", result);
            //JSONObject jsonObject = (JSONObject) result.get("data");
            if (Integer.parseInt(result.get("code").toString()) != 0) {
                failCount += 1;
                mapParam.put("description", result.get("msg"));
                mapParam.put("operator", currentUser.getLoginName());
                mapParam.put("createSystem", "0");//平台系统
                mapParam.put("orderType", "2");//提现
                mapParam.put("exceptionId", GenerateOrderNo.getInstance().Generate(StaticConstants.PREFIX_EXCEPTION_ID));
                //AsyncManager.me().execute(AsyncFactory.recordExceptionOrder(mapParam));
            } else if (Integer.parseInt(result.get("code").toString()) == 0) {
                successCount += 1;
            }
        }
        logger.info("商户{}提现订单提交成功【提交成功】订单数：{} >>>" + "【提交失败】订单数：{}", ShiroUtils.getSysUser().getLoginName(), successCount, (listBean.size() - successCount));
        return AjaxResult.success("已提交成功");
    }

    @Log(title = "导入提款信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<WithdrawalBean> util = new ExcelUtil<WithdrawalBean>(WithdrawalBean.class);
        List<WithdrawalBean> userList = util.importExcel(file.getInputStream());
        userList.forEach(user -> {
            PropertyValidateUtil.validate(user);
        });
        ;
        String operName = ShiroUtils.getSysUser().getLoginName();

        return batchWithdrawal(userList);
    }

    @GetMapping("/importTemplate")
    @ResponseBody
    public AjaxResult importTemplate() {
        ExcelUtil<WithdrawalBean> util = new ExcelUtil<WithdrawalBean>(WithdrawalBean.class);
        return util.importTemplateExcel("批量提款数据");
    }

    @Log(title = "内充申请-人民币手动充值", businessType = BusinessType.INSERT)
    @PostMapping("/withdrawal/rechargeAddLocation")
    @ResponseBody
    @RepeatSubmit
    public AjaxResult rechargeAddLocation(AlipayWithdrawEntity alipayWithdrawEntity) {
        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        String userId = currentUser.getMerchantId();
        AlipayUserRateEntity rateEntity = alipayUserRateEntityService.findRateByType(userId, "BANK_SVIP");
        AlipayUserInfo userInfo = new AlipayUserInfo();
        userInfo.setUserId(userId);
        List<AlipayUserInfo> alipayUserInfos = merchantInfoEntityService.selectMerchantInfoEntityList(userInfo);
        SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
        String userid = rateEntity.getUserId();
        String key = alipayUserInfos.get(0).getPayPasword();//交易密钥
        String publicKey = alipayUserInfos.get(0).getPublicKey();
        String amount = alipayWithdrawEntity.getAmount().toString();
        String orderId = GenerateOrderNo.getInstance().Generate("BANK_SVIP");
        String post = postWit(amount, userid, rateEntity.getPayTypr(), orderId, key, publicKey);
        return AjaxResult.success();
    }


    String postWit(String amount, String userId, String payType, String orderId, String key, String publicKey) {
        return postWit(amount, userId, payType, orderId, key, publicKey, null);
    }

    /**
     * 商户发起申请提现
     */
    @GetMapping("/recharge")
    public String recharge(ModelMap mmap) {
        SysUser sysUser = ShiroUtils.getSysUser();
        AlipayUserFundEntity alipayUserFundEntity = alipayUserFundEntityService.findAlipayUserFundByUserId(sysUser.getMerchantId());
        mmap.put("userFund", alipayUserFundEntity);
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setProductId("RECHARGE");
        List<AlipayProductEntity> list = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        if (CollUtil.isNotEmpty(list)) {
            AlipayProductEntity first = CollUtil.getFirst(list);
            if ("1".equals(first.getRetain1())) {
                return prefix + "/rechargeLocation";
            }
        }
        return prefix + "/recharge";
    }

    /**
     * 商户发起申请提现
     */
    @GetMapping("/rechargeAdd")
    public String rechargeAdd(ModelMap mmap) {
        SysUser sysUser = ShiroUtils.getSysUser();
        AlipayUserFundEntity alipayUserFundEntity = alipayUserFundEntityService.findAlipayUserFundByUserId(sysUser.getMerchantId());
        mmap.put("userFund", alipayUserFundEntity);
        return prefix + "/rechargeAddLocation";
    }


    //商户查询银行卡
    @GetMapping("/bank/view")
    public String bankCard() {
        return prefix + "/bank";
    }


    //下线数据
    @GetMapping("/data/view")
    public String agent() {
        return prefix + "/agent";
    }

    /**
     * 查询下线代理商户
     */
    @PostMapping("/data/list")
    @ResponseBody
    public TableDataInfo agentList(AlipayUserInfo alipayUserInfo) {
        SysUser sysUser = ShiroUtils.getSysUser();
        alipayUserInfo.setUserId(sysUser.getMerchantId());
        List<String> agentList = merchantInfoEntityServiceImpl.selectNextAgentByParentId(alipayUserInfo.getUserId());
        String str = CollUtil.getFirst(agentList);
        List list1 = new ArrayList(Arrays.asList(str.split(",")));
        if (str.split(",").length > 2) {
            list1.remove(0);
            list1.remove(0);
        }
        if (StrUtil.isNotBlank(alipayUserInfo.getUserId())) {
            if (list1.contains(alipayUserInfo.getUserId())) {
                list1.clear();
                list1 = new ArrayList();
                list1.add(alipayUserInfo.getUserId());
            }
        }
        startPage();
        List<AlipayUserInfo> list = merchantInfoEntityService.selectAgentByMerchantId(list1);
        return getDataTable(list);
    }

    //下线订单
    @GetMapping("/agent/order/view")
    public String agentOrder() {
        return prefix + "/agent_order";
    }

    @Autowired
    private ISysUserService userService;
    /**
     * 查询商户订单
     */
    @Autowired
    IMerchantInfoEntityService merchantInfoEntityServiceImpl;

    @PostMapping("/agent/order/list")
    @ResponseBody
    public TableDataInfo agentOrder(AlipayDealOrderApp alipayDealOrderApp) {
        SysUser sysUser = ShiroUtils.getSysUser();
        alipayDealOrderApp.setOrderAccount(sysUser.getMerchantId());
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        List<AlipayProductEntity> productlist = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        //查询商户所有的下级用户
        List<String> agentList = merchantInfoEntityServiceImpl.selectNextAgentByParentId(alipayDealOrderApp.getOrderAccount());
        String str = CollUtil.getFirst(agentList);
        List list1 = new ArrayList(Arrays.asList(str.split(",")));
        if (str.split(",").length > 2) {
            list1.remove(0);
            list1.remove(0);
        }
        if (StrUtil.isNotBlank(alipayDealOrderApp.getUserName())) {
            if (list1.contains(alipayDealOrderApp.getUserName())) {
                list1.clear();
                list1 = new ArrayList();
                list1.add(alipayDealOrderApp.getUserName());
            }
        }
        startPage();
        List<AlipayDealOrderApp> list = alipayDealOrderAppService.selectSubMembersOrderList(list1, alipayDealOrderApp);
        ConcurrentHashMap<String, AlipayProductEntity> prCollect = productlist.stream().collect(Collectors.toConcurrentMap(AlipayProductEntity::getProductId, Function.identity(), (o1, o2) -> o1, ConcurrentHashMap::new));
        SysUser user = new SysUser();
        List<SysUser> sysUsers = userService.selectUserList(user);
        ConcurrentHashMap<String, SysUser> userCollect = new ConcurrentHashMap<String, SysUser>();
        for (SysUser user1 : sysUsers) {
            if (StrUtil.isNotBlank(user1.getMerchantId())) {
                userCollect.put(user1.getMerchantId(), user1);
            }
        }
        for (AlipayDealOrderApp order : list) {
            AlipayProductEntity product = prCollect.get(order.getRetain1());
            order.setUserName(userCollect.get(order.getOrderAccount()).getUserName());
            if (ObjectUtil.isNotNull(product)) {
                order.setRetain1(product.getProductName());
            }
        }
        prCollect = null;
        userCollect = null;
        return getDataTable(list);
    }

    /**
     * 显示统计table
     */
    @GetMapping("/statistics/merchant/admin/tableAgent")
    public String showTable() {
        return prefix + "/currentTableAgent";
    }

    /**
     * 显示统计table
     */
    @GetMapping("/statistics/merchant/admin/table")
    public String showTable1() {
        return prefix + "/currentTable";
    }

    @GetMapping("/withdrawal/getRateUsdtFee")
    @ResponseBody
    public AjaxResult getRateUsdtFee(HttpServletRequest request, String addressType) {
        String amountCNY = request.getParameter("amountCNY");//人民币充值金额
        if (StrUtil.isEmpty(amountCNY)) {
            return AjaxResult.error("数据错误");
        }
        SysDictData dictData = new SysDictData();
        dictData.setCssClass(addressType);//usdt 汇率字典 type
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        Integer aDouble = Integer.valueOf(amountCNY);
        for (SysDictData date : list) {
            String dictLabel = date.getDictLabel();
            String[] split = dictLabel.split("-");
            Double min = Double.valueOf(split[0]);//兑换区间最小
            Double max = Double.valueOf(split[1]);//兑换区间最大  包括
            if (aDouble >= min) {
                if (aDouble <= max) {
                    Random ram = new Random();
                    int i = ram.nextInt(5);
                    while (i == 0) {
                        i = ram.nextInt(5);
                    }
                    aDouble = aDouble + i;
                    double v = 0;
                    try {
                        BigDecimal onlineRate = new BigDecimal(BigInteger.ZERO);
                        BigDecimal locatuonRate = BigDecimal.valueOf(Double.valueOf(date.getDictValue()));
                        onlineRate = BigDecimal.valueOf(Double.valueOf(getRateFee()));
                        v = onlineRate.add(locatuonRate).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    } catch (Exception e) {
                        return AjaxResult.error("汇率错误");
                    }
                    double div = Arith.div(aDouble, v, 2);//当前需要转入的 usdt 个数
                    Map map = new HashMap();
                    map.put("amount", div);
                    map.put("rate", v);
                    return AjaxResult.success(map);
                }
            }
        }
        return AjaxResult.error("汇率错误");
    }

    /**
     * 保存提现提案
     */
    @Log(title = "内充申请-自动回调", businessType = BusinessType.INSERT)
    @PostMapping("/withdrawal/recharge")
    @ResponseBody
    @RepeatSubmit
    public AjaxResult recharge(AlipayWithdrawEntity alipayWithdrawEntity) {
        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        String payPassword = (String) alipayWithdrawEntity.getParams().get("payPassword");
        String verify = passwordService.encryptPassword(currentUser.getLoginName(), payPassword, currentUser.getSalt());
        if (!currentUser.getFundPassword().equals(verify)) {
            return AjaxResult.error("密码验证失败");
        }
        String userId = currentUser.getMerchantId();
        AlipayUserRateEntity rateEntity = alipayUserRateEntityService.findRateByType(userId, "RECHARGE");
        AlipayUserInfo userInfo = new AlipayUserInfo();
        userInfo.setUserId(userId);
        List<AlipayUserInfo> alipayUserInfos = merchantInfoEntityService.selectMerchantInfoEntityList(userInfo);
        SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
        String userid = rateEntity.getUserId();
        String key = alipayUserInfos.get(0).getPayPasword();//交易密钥
        String publicKey = alipayUserInfos.get(0).getPublicKey();
        String amount = alipayWithdrawEntity.getAmount().toString();
        String orderId = GenerateOrderNo.getInstance().Generate(StringUtils.equals(alipayWithdrawEntity.getAddressType() == null ?
                "" : alipayWithdrawEntity.getAddressType(), "trc") ? "USDT_TRC" : "USDT_ERC");
        orderId = orderId + "_" + userId;
        String post = postWit(amount, userid, rateEntity.getPayTypr(), orderId, key, publicKey, alipayWithdrawEntity.getUSDTRate());
        JSONObject json = JSONObject.parseObject(post);
        String result = json.getString("success");
        if (StrUtil.isNotEmpty(result) && result.equals("true")) {
            //新建常规订单已经成功
            //下面新建usdt充值订单   使用固定usdt  转人民币内充账号
            String usdTamount = alipayWithdrawEntity.getUSDTamount();//usdt充值金额
            AlipayUserInfo userInfokent = new AlipayUserInfo();
            userInfokent.setUserId("KENTUSDTMANAGE");
            List<AlipayUserInfo> alipayUserInfokent = merchantInfoEntityService.selectMerchantInfoEntityList(userInfokent);
            String keykent = alipayUserInfokent.get(0).getPayPasword();//交易密钥
            String publicKeykent = alipayUserInfokent.get(0).getPublicKey();
            AlipayUserRateEntity usdt = alipayUserRateEntityService.findRateByType("KENTUSDTMANAGE", "USDT");
            String kentusdtmanage = postWit(usdTamount, "KENTUSDTMANAGE", usdt.getPayTypr(), orderId, keykent, publicKeykent, alipayWithdrawEntity.getUSDTRate());
            JSONObject json1 = JSONObject.parseObject(kentusdtmanage);
            String result1 = json1.getString("success");
            if (StrUtil.isNotEmpty(result1) && result1.equals("true")) {
                String result2 = json1.getString("result");
                JSONObject json2 = JSONObject.parseObject(result2);
                return AjaxResult.success(json2.getString("returnUrl"));
            }
            return AjaxResult.error("暂无收款地址");
        } else {
            return AjaxResult.error("暂无收款地址");
        }
    }

    @Value("${otc.wit.url}")
    private String witUrl;

    String postWit(String amount, String userId, String payType, String orderId, String key, String publicKey, String url) {
        Map<String, Object> parMap = new HashMap<>();
        parMap.put("amount", amount);
        parMap.put("appId", userId);
        parMap.put("applyDate", DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN));
        parMap.put("notifyUrl", "http://starpay168.com:5055");
        if (StrUtil.isNotEmpty(url)) {
            parMap.put("notifyUrl", url);
        }
        parMap.put("pageUrl", "http://starpay168.com:5055");
        parMap.put("orderId", orderId);
        parMap.put("passCode", payType);
        parMap.put("subject", amount);
        String createParam = MapDataUtil.createParam(parMap);
        logger.info("签名前请求串：" + createParam);
        String md5 = HashKit.md5((createParam + key));
        logger.info("签名：" + md5);
        parMap.put("sign", md5);
        String createParam2 = MapDataUtil.createParam(parMap);
        logger.info("加密前字符串：" + createParam2);
        String publicEncrypt = RSAUtils.publicEncrypt(createParam2, publicKey);
        logger.info("加密后字符串：" + publicEncrypt);
        Map<String, Object> postMap = new HashMap<String, Object>();
        postMap.put("cipherText", publicEncrypt);
        postMap.put("userId", userId);
        logger.info("请求参数：" + postMap.toString());
        String post = HttpUtil.post(witUrl, postMap);
        logger.info("相应结果集：" + post);
        return post;
    }

    /**
     * 后台管理员商户交易订单统计（仅当天数据）
     */
    @PostMapping("/statistics/merchant/admin/order")
    @ResponseBody
    public TableDataInfo dayStat(StatisticsEntity statisticsEntity) {
        SysUser sysUser = ShiroUtils.getSysUser();

        statisticsEntity.setUserAgent(sysUser.getLoginName());
        //过滤出下级id
        List<String> subUserIds = merchantInfoEntityService.selectNextAgentByParentId(sysUser.getLoginName());
//        String userIdStr = subUserIds.get(0)==null ? "":subUserIds.get(0);
//        userIdStr = userIdStr.replace("$,","");
        if (CollectionUtils.isNotEmpty(subUserIds)) {

            String[] idArray = subUserIds.get(0).split(",");
            subUserIds = Arrays.asList(idArray);
        } else {
            subUserIds = Lists.newArrayList();
            subUserIds.add(sysUser.getLoginName());
        }
        if (StringUtils.isEmpty(sysUser.getMerchantId())) {
            throw new BusinessException("获取商户账户异常，请联系管理员");
        }
        statisticsEntity.setUserId(sysUser.getMerchantId());
        startPage();
        List<StatisticsEntity> list = alipayDealOrderAppService.selectMerchantStatisticsDataByDay(statisticsEntity, DateUtils.dayStart(), DateUtils.dayEnd(), subUserIds);
        List<StatisticsEntity> dataList = new ArrayList();
        for (StatisticsEntity statis : list) {
            if (!statis.getUserId().equals("所有")) {
                dataList.add(statis);
            }
        }
        return getDataTable(dataList);
    }

    String getRateFee() {
        Set<String> set = cache.keySet();
        for (String key : set) {
            String[] split = key.split(MARK);
            String s = split[1];
            if (DateUtil.isExpired(DateUtil.parse(s), DateField.SECOND, 20, new Date())) {
                cache.remove(key);
            }
            ;
        }
        Object o = this.cache.get(RATE_KEY + DateUtils.getTime());
        if (null == o) {
            String rateBull = getRate("buy");
            String rateSell = getRate("sell");
            BigDecimal bigDecimal = new BigDecimal(rateBull);
            BigDecimal bigDecimal1 = new BigDecimal(rateSell);
            if (bigDecimal.compareTo(bigDecimal1) == -1) {
                this.cache.put(RATE_KEY + DateUtils.getTime(), rateBull);
                return rateBull;
            } else {
                this.cache.put(RATE_KEY + DateUtils.getTime(), rateSell);
                return rateSell;
            }
        }
        return null;
    }

    @Value("${otc.usdt.rate}")
    private String otcRate;

    String getRate(String type) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", type);
        String post = null;
        try {
            post = HttpUtil.get(otcRate, data);
        } catch (Exception e) {
            logger.error("获取汇率失败", e);
            return null;
        }
        return post;
    }
}

