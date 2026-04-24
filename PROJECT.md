# CancerApp 项目文档

本文档描述项目定位、技术栈、数据模型、路由，并记录**设计与实现思路**，便于协作与迭代。

## 1. 项目定位

**CancerApp（化疗营养助手）** 面向化疗期间的营养与用药辅助：患者档案、化疗方案、营养相互作用、每日方案、不良反应、保健品风险自查等。产品形态为 **Spring Boot 服务端渲染的 Web 应用**（Thymeleaf 模板 + 移动端友好布局）。

主入口类：`com.nfy.cancerapp.RecordAppApplication`（可执行 JAR 构建名为 `CancerApp`）。

---

## 1.1 产品功能（八个界面）

与当前模板、控制器对齐的**单页职责**如下（用户向说明见根目录 **HELP.md**）。

| # | 界面 | 能力要点 |
|---|------|----------|
| 1 | **功能首页** `home.html` | 当前化疗周期阶段文案、今日饮食/营养风险提醒、周期与今日用药提示、快速入口（查风险 `/interaction`、记反应 `/reaction`、看方案 `/regimen` 等） |
| 2 | **患者信息** `patient.html` / `patient-form.html` | 年龄、身高、体重、肿瘤类型、肝肾、过敏、饮食禁忌；**`NutritionNeedsCalculator`** 在保存时写入估算 **热量、蛋白质** |
| 3 | **化疗方案** `regimen*.html` 等 | 模板选方案、进度、参考用药；与 **`SupplementRuleService`** 药名匹配联动 |
| 4 | **营养-药物相互作用** `interaction.html` | **红 / 黄 / 绿** 分区：禁用或避免、慎用与间隔、相对推荐或安全 |
| 5 | **每日营养方案** `daily-plan.html` | 化疗前/中/后阶段指引；高蛋白高热量易消化；**`DailyNutritionPlanService.mergeDailyPlanFromReaction`** 在保存不良反应后合并**当日**计划 |
| 6 | **不良反应** `reaction.html` | 恶心、呕吐、腹泻等记录与历史；与每日营养联动 |
| 7 | **保健品风险自查** `supplement-check.html` | 关键字搜索 + 与本人方案药名匹配；**可用 / 慎用 / 禁用** 类展示 |
| 8 | **我的 / 设置** `settings.html` | 模块跳转、帮助与关于、免责声明、退出登录 |

登录成功默认 **`redirect:/home`**；**`GET /quick-home`** 兼容重定向到 **`/home`**。

---

## 2. 技术栈

| 类别 | 选型 |
|------|------|
| 语言 / JDK | Java 17 |
| 框架 | Spring Boot 3.5.x |
| Web | Spring Web、Thymeleaf |
| 数据库 | MySQL 8 |
| 数据访问 | **MyBatis-Plus**（`mybatis-plus-spring-boot3-starter`），`@MapperScan` 见 `MybatisPlusConfig` |
| 开发体验 | spring-boot-devtools |

**错误展示**：`templates/error.html`；`GlobalExceptionHandler` 处理 `DataAccessException`、通用 `Exception` 及 `NoResourceFoundException`（404），避免 Whitelabel；`application.properties` 中 `server.error.whitelabel.enabled=false`、`server.error.include-message=always`（生产可按需收紧）。

---

## 3. 目录结构（核心）

```
demo/
├── pom.xml
├── PROJECT.md
├── src/main/java/com/nfy/cancerapp/
│   ├── RecordAppApplication.java
│   ├── bootstrap/
│   │   └── SupplementRuleDataLoader.java   # 首次启动 supplement_rule 为空时写入示例规则
│   ├── config/
│   │   ├── WebMvcConfig.java
│   │   └── MybatisPlusConfig.java
│   ├── controller/
│   ├── model/
│   ├── mapper/
│   ├── service/
│   │   ├── PatientService.java              # 含 NutritionNeedsCalculator 写入热量/蛋白
│   │   ├── HomeDashboardService.java        # 首页阶段描述、营养风险摘要
│   │   ├── QuickHomeService.java            # 周期序号、今日用药提示（首页沿用）
│   │   ├── RegimenService.java
│   │   ├── AdverseReactionService.java      # 保存反应后触发当日营养合并
│   │   ├── DailyNutritionPlanService.java
│   │   ├── NutritionNeedsCalculator.java
│   │   └── SupplementRuleService.java
│   ├── regimen/
│   │   └── RegimenTemplateCatalog.java
│   └── web/
│       ├── LoginPatient.java               # Session → Patient，统一「请先登录」
│       └── GlobalExceptionHandler.java
└── src/main/resources/
    ├── application.properties
    ├── db/create_table_production.sql
    ├── db/migration_add_regimen_template_code.sql
    └── templates/
        ├── welcome.html, login.html
        ├── home.html                          # 功能首页（主入口）
        ├── quick-home.html                    # 遗留；路由已重定向 /home
        ├── patient.html, patient-form.html
        ├── regimen*.html, med-form.html
        ├── reaction.html, reaction-form.html
        ├── daily-plan.html, daily-plan-form.html
        ├── supplement-check.html, interaction.html, settings.html
        ├── error.html
        └── …
```

---

## 4. 运行与配置

1. 本地安装 **MySQL**，执行 `src/main/resources/db/create_table_production.sql`（库名默认 `cancer_app`）。**若库是早期版本**，再执行 `db/migration_add_regimen_template_code.sql`。
2. 配置 `spring.datasource.*`（**勿将真实密码提交到公开仓库**）。
3. 端口默认 **8081**；首次启动若 `supplement_rule` 表为空，**`SupplementRuleDataLoader`** 会自动插入示例规则（可重复启动，已有数据则跳过）。
4. 构建：`mvn -q package`，产物 `CancerApp.jar`。

---

## 5. HTTP 路由一览

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 欢迎页 `welcome.html` |
| GET/POST | `/login` | 登录；POST 参数 `name`（及示意用 `password`）；成功后 **`redirect:/home`**；`ensureExists` 患者 |
| GET | `/home` | **功能首页**（阶段、风险、快速入口） |
| GET | `/quick-home` | **重定向 `/home`**（兼容旧书签） |
| GET | `/patient`、`/patient/edit`、POST `/patient/save` | 患者档案 |
| GET | `/regimen` 及 `/regimen/*` | 化疗方案（选择模板、进度、用药参考等），见 §9.2 |
| **GET** | **`/reaction`** | **不良反应列表** |
| **GET** | **`/reaction/edit`** | **新增/编辑（`?id=`）** |
| **POST** | **`/reaction/save`**、**`/reaction/delete`** | **保存、删除** |
| **GET** | **`/daily-plan`** | **每日营养方案列表** |
| **GET** | **`/daily-plan/edit`** | **新增/编辑（`?id=`）** |
| **POST** | **`/daily-plan/save`**、**`/daily-plan/delete`** | **保存、删除** |
| **GET** | **`/supplement-check`** | **保健品自查（`?q=` 关键字）；展示「按药名关联」规则** |
| **GET** | **`/interaction`** | **营养与药物总览：方案药名 + 匹配规则 + 通用慎用摘录** |
| **GET** | **`/settings`** | **我的：链接聚合、免责声明、退出登录** |
| **POST** | **`/settings/logout`** | **退出（销毁 Session）** |
| GET | `/api/hello` | 探活 |

**登录要求**：除 `/`、`/login`、`/settings`（未登录也可看说明）外，上述业务模块控制器内均通过 **`LoginPatient.require`** 校验；未登录重定向 `/login`。

---

## 6. 数据库模型

脚本：`src/main/resources/db/create_table_production.sql`。

| 表名 | 用途 |
|------|------|
| `patient` | 患者基本信息 |
| `chemo_regimen` | 化疗方案（含 `template_code`） |
| `chemo_medication` | 方案参考用药 |
| `adverse_reaction` | 不良反应（可选 `regimen_id`） |
| `supplement_rule` | 保健品与化疗相互作用规则（全局库，启动时可种子数据） |
| `daily_nutrition_plan` | 每日营养方案（可选 `regimen_id`） |

---

## 7. 前端页面（templates）

- **已串联**：`welcome` / `login` / **`home`（主首页）**、`patient*`、完整 **regimen** 流、**reaction**、**daily-plan**、**supplement-check**、**interaction**、**settings**、**error**。
- **遗留**：`quick-home.html`（路由已不重渲染）、`index.html`、`mood.html`、`list.html`、`success.html` 等旧 demo，无对应路由可忽略。

---

## 8. 安全与合规提示

- 登录 **无密码校验**，演示用。
- 以 **姓名** 绑定会话与患者；生产环境应使用独立用户 ID。
- 全站文案强调：**非医疗决策依据**，以医嘱为准。

---

## 9. 设计与实现思路（纪要）

### 9.1 数据访问与异常

- MyBatis-Plus `BaseMapper` + 少量 `@Select`（快捷首页）。
- **`LoginPatient`**：统一 Session 解析与 flash 提示，减少各 Controller 重复代码。
- **`GlobalExceptionHandler`**：数据库异常提示检查迁移脚本；通用异常走 `error.html`，避免 Whitelabel。

### 9.2 化疗方案模块（患者向）

- 从 **`RegimenTemplateCatalog`** 选择常见方案；`template_code` + 自动生成参考用药；进度单独页维护。详见前文各版说明，此处不重复字段表。

### 9.3 不良反应模块

- **归属**：`patient_id` 必为当前登录患者；可选 **`regimen_id`** 须 `getOwnedRegimen` 校验。
- **交互**：反应类型使用 **预设中文选项**（恶心、腹泻等），降低输入门槛；严重程度 1～5 可选。
- **与每日营养**：保存成功后调用 **`DailyNutritionPlanService.mergeDailyPlanFromReaction`**，将提示合并进 **当日** `daily_nutrition_plan`（无则跳过），供用户在每日营养页查看或再编辑。

### 9.4 每日营养方案模块

- **归属**：`patient_id` 归属校验；可选 **`regimen_id`**。
- **字段**：日期、`phase`（PRE/DURING/POST）、主要针对问题、热量/蛋白质、饮食建议、示例菜谱——面向患者自述或摘录营养师建议。
- **展示**：列表页可带 **阶段指引块**（化疗前/中/后）与 **最近一条不良反应** 提示；与不良反应保存后的合并逻辑配合使用。

### 9.4.1 患者营养需求估算

- **`NutritionNeedsCalculator`**：在 **`PatientService.saveOrUpdate`** 中，当年龄、性别、身高、体重齐全时写入 **`patient.kcalNeed`、`patient.proteinNeedG`**；表单与详情页展示「仅供参考」。

### 9.5 保健品规则与自查

- **`SupplementRuleService`**：`searchByUserKeyword` 对 `supplement_name`、`ingredient_tags`、`drug_keyword` 做 `LIKE`；`matchRulesForPatientDrugs` 用患者所有方案下 **去重药名** 与规则中 **逗号分隔** 的药物关键词做包含匹配（不区分大小写）。
- **`SupplementRuleDataLoader`**：`supplement_rule` 为空时插入多条 **教育向** 示例（非完整临床指南）。
- **`/supplement-check`**：同时展示「搜索命中」与「与本人方案药名相关的规则」；链到 **interaction** 总览。

### 9.6 营养与药物总览（interaction）

- **输入数据**：`RegimenService.listDistinctDrugNamesForPatient`。
- **输出**：① 药名列表；② 按风险等级分为 **禁用/避免（红）**、**慎用与间隔（黄）**、**相对安全或推荐（绿）**；③ 可含通用慎用摘录。
- **逻辑边界**：无方案或无药名时明确空态，引导去 **regimen** 或 **supplement-check**。

### 9.7 功能首页（home）

- **`HomeDashboardService`**：`describeChemoStage`、`buildNutritionRiskHint`（结合方案与 `matchRulesForPatientDrugs` 等）。
- **`QuickHomeService`**：当前周期展示、今日用药提示（与真实日历对齐仍为可增强项）。

### 9.8 设置页

- 聚合跳转患者信息、各业务模块；内嵌 **帮助与使用指南**、**关于本系统**、免责声明；**POST 退出** 销毁 Session，登录页可展示 `notice`。

### 9.9 未做或可增强

- 首页「今日用药」与真实日历、方案开始日完全对齐。
- 规则库后台维护、按医院导入。
- 全局登录拦截器（`WebMvcConfig`）与白名单。

---

*文档更新：2026-03-29（八界面产品说明、HOME 主入口、HELP 用户指南同步）。*
