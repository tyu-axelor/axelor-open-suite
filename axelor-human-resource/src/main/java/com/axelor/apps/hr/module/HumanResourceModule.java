/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2024 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.apps.hr.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.account.db.repo.PartnerAccountRepository;
import com.axelor.apps.account.service.analytic.AnalyticMoveLineGenerateRealServiceImpl;
import com.axelor.apps.account.service.batch.BatchCreditTransferExpensePayment;
import com.axelor.apps.account.service.config.AccountConfigService;
import com.axelor.apps.account.service.move.MoveValidateServiceImpl;
import com.axelor.apps.bankpayment.service.bankorder.BankOrderCancelServiceImpl;
import com.axelor.apps.bankpayment.service.bankorder.BankOrderLineOriginServiceImpl;
import com.axelor.apps.bankpayment.service.bankorder.BankOrderMergeServiceImpl;
import com.axelor.apps.bankpayment.service.bankorder.BankOrderValidationServiceImpl;
import com.axelor.apps.bankpayment.service.move.MoveReverseServiceBankPaymentImpl;
import com.axelor.apps.base.db.repo.UserBaseRepository;
import com.axelor.apps.base.service.batch.MailBatchService;
import com.axelor.apps.hr.db.repo.EmployeeHRRepository;
import com.axelor.apps.hr.db.repo.EmployeeRepository;
import com.axelor.apps.hr.db.repo.EmploymentContractHRRepository;
import com.axelor.apps.hr.db.repo.EmploymentContractRepository;
import com.axelor.apps.hr.db.repo.ExpenseHRRepository;
import com.axelor.apps.hr.db.repo.ExpenseLineHRRepository;
import com.axelor.apps.hr.db.repo.ExpenseLineRepository;
import com.axelor.apps.hr.db.repo.ExpenseRepository;
import com.axelor.apps.hr.db.repo.HrBatchHRRepository;
import com.axelor.apps.hr.db.repo.HrBatchRepository;
import com.axelor.apps.hr.db.repo.MedicalVisitHRRepository;
import com.axelor.apps.hr.db.repo.MedicalVisitRepository;
import com.axelor.apps.hr.db.repo.PartnerHRRepository;
import com.axelor.apps.hr.db.repo.ProjectHRRepository;
import com.axelor.apps.hr.db.repo.ProjectPlanningTimeHRRepository;
import com.axelor.apps.hr.db.repo.ProjectTaskHRRepository;
import com.axelor.apps.hr.db.repo.TSTimerRepository;
import com.axelor.apps.hr.db.repo.TimesheetHRRepository;
import com.axelor.apps.hr.db.repo.TimesheetLineHRRepository;
import com.axelor.apps.hr.db.repo.TimesheetLineRepository;
import com.axelor.apps.hr.db.repo.TimesheetRepository;
import com.axelor.apps.hr.db.repo.TimesheetTimerHRRepository;
import com.axelor.apps.hr.db.repo.UserHRRepository;
import com.axelor.apps.hr.event.ICalendarEventObserver;
import com.axelor.apps.hr.service.BankCardService;
import com.axelor.apps.hr.service.BankCardServiceImpl;
import com.axelor.apps.hr.service.EmployeeComputeStatusService;
import com.axelor.apps.hr.service.EmployeeComputeStatusServiceImpl;
import com.axelor.apps.hr.service.EmployeeFileDMSService;
import com.axelor.apps.hr.service.EmployeeFileDMSServiceImpl;
import com.axelor.apps.hr.service.HRDashboardService;
import com.axelor.apps.hr.service.HRDashboardServiceImpl;
import com.axelor.apps.hr.service.MedicalVisitService;
import com.axelor.apps.hr.service.MedicalVisitServiceImpl;
import com.axelor.apps.hr.service.MedicalVisitWorkflowService;
import com.axelor.apps.hr.service.MedicalVisitWorkflowServiceImpl;
import com.axelor.apps.hr.service.SchedulerCreationService;
import com.axelor.apps.hr.service.SchedulerCreationServiceImpl;
import com.axelor.apps.hr.service.UnitConversionForProjectService;
import com.axelor.apps.hr.service.UnitConversionForProjectServiceImpl;
import com.axelor.apps.hr.service.analytic.AnalyticMoveLineGenerateRealServiceHrImpl;
import com.axelor.apps.hr.service.app.AppHumanResourceService;
import com.axelor.apps.hr.service.app.AppHumanResourceServiceImpl;
import com.axelor.apps.hr.service.app.AppTimesheetService;
import com.axelor.apps.hr.service.app.AppTimesheetServiceImpl;
import com.axelor.apps.hr.service.bankorder.BankOrderCancelServiceHRImpl;
import com.axelor.apps.hr.service.bankorder.BankOrderLineOriginServiceHRImpl;
import com.axelor.apps.hr.service.bankorder.BankOrderMergeHRServiceImpl;
import com.axelor.apps.hr.service.bankorder.BankOrderValidationServiceHRImpl;
import com.axelor.apps.hr.service.batch.BatchCreditTransferExpensePaymentHR;
import com.axelor.apps.hr.service.batch.MailBatchServiceHR;
import com.axelor.apps.hr.service.config.AccountConfigHRService;
import com.axelor.apps.hr.service.employee.EmployeeFetchService;
import com.axelor.apps.hr.service.employee.EmployeeFetchServiceImpl;
import com.axelor.apps.hr.service.employee.EmployeeService;
import com.axelor.apps.hr.service.employee.EmployeeServiceImpl;
import com.axelor.apps.hr.service.employee.EmploymentAmendmentTypeService;
import com.axelor.apps.hr.service.employee.EmploymentAmendmentTypeServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseAnalyticService;
import com.axelor.apps.hr.service.expense.ExpenseAnalyticServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseCancellationService;
import com.axelor.apps.hr.service.expense.ExpenseCancellationServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseCheckResponseService;
import com.axelor.apps.hr.service.expense.ExpenseCheckResponseServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseComputationService;
import com.axelor.apps.hr.service.expense.ExpenseComputationServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseConfirmationService;
import com.axelor.apps.hr.service.expense.ExpenseConfirmationServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseCreateService;
import com.axelor.apps.hr.service.expense.ExpenseCreateServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseCreateWizardService;
import com.axelor.apps.hr.service.expense.ExpenseCreateWizardServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseFetchMoveService;
import com.axelor.apps.hr.service.expense.ExpenseFetchMoveServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseFetchPeriodService;
import com.axelor.apps.hr.service.expense.ExpenseFetchPeriodServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseInvoiceLineService;
import com.axelor.apps.hr.service.expense.ExpenseInvoiceLineServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseKilometricService;
import com.axelor.apps.hr.service.expense.ExpenseKilometricServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseLimitService;
import com.axelor.apps.hr.service.expense.ExpenseLimitServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseLineCreateService;
import com.axelor.apps.hr.service.expense.ExpenseLineCreateServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseLineService;
import com.axelor.apps.hr.service.expense.ExpenseLineServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseLineToolService;
import com.axelor.apps.hr.service.expense.ExpenseLineToolServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseLineUpdateService;
import com.axelor.apps.hr.service.expense.ExpenseLineUpdateServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseMoveReverseServiceImpl;
import com.axelor.apps.hr.service.expense.ExpensePaymentService;
import com.axelor.apps.hr.service.expense.ExpensePaymentServiceImpl;
import com.axelor.apps.hr.service.expense.ExpensePrintService;
import com.axelor.apps.hr.service.expense.ExpensePrintServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseProofFileService;
import com.axelor.apps.hr.service.expense.ExpenseProofFileServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseRefusalService;
import com.axelor.apps.hr.service.expense.ExpenseRefusalServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseToolService;
import com.axelor.apps.hr.service.expense.ExpenseToolServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseValidateService;
import com.axelor.apps.hr.service.expense.ExpenseValidateServiceImpl;
import com.axelor.apps.hr.service.expense.ExpenseVentilateService;
import com.axelor.apps.hr.service.expense.ExpenseVentilateServiceImpl;
import com.axelor.apps.hr.service.expense.expenseline.ExpenseLineCheckResponseService;
import com.axelor.apps.hr.service.expense.expenseline.ExpenseLineCheckResponseServiceImpl;
import com.axelor.apps.hr.service.expense.expenseline.ExpenseLineResponseComputeService;
import com.axelor.apps.hr.service.expense.expenseline.ExpenseLineResponseComputeServiceImpl;
import com.axelor.apps.hr.service.extra.hours.ExtraHoursService;
import com.axelor.apps.hr.service.extra.hours.ExtraHoursServiceImpl;
import com.axelor.apps.hr.service.leave.IncrementLeaveService;
import com.axelor.apps.hr.service.leave.IncrementLeaveServiceImpl;
import com.axelor.apps.hr.service.leave.LeaveExportService;
import com.axelor.apps.hr.service.leave.LeaveExportServiceImpl;
import com.axelor.apps.hr.service.leave.LeaveLineService;
import com.axelor.apps.hr.service.leave.LeaveLineServiceImpl;
import com.axelor.apps.hr.service.leave.LeaveRequestComputeDurationService;
import com.axelor.apps.hr.service.leave.LeaveRequestComputeDurationServiceImpl;
import com.axelor.apps.hr.service.leave.LeaveRequestEventService;
import com.axelor.apps.hr.service.leave.LeaveRequestEventServiceImpl;
import com.axelor.apps.hr.service.leave.LeaveRequestMailService;
import com.axelor.apps.hr.service.leave.LeaveRequestMailServiceImpl;
import com.axelor.apps.hr.service.leave.LeaveRequestManagementService;
import com.axelor.apps.hr.service.leave.LeaveRequestManagementServiceImpl;
import com.axelor.apps.hr.service.leave.LeaveRequestService;
import com.axelor.apps.hr.service.leave.LeaveRequestServiceImpl;
import com.axelor.apps.hr.service.leave.LeaveRequestWorkflowService;
import com.axelor.apps.hr.service.leave.LeaveRequestWorkflowServiceImpl;
import com.axelor.apps.hr.service.leave.LeaveValueProrataService;
import com.axelor.apps.hr.service.leave.LeaveValueProrataServiceImpl;
import com.axelor.apps.hr.service.leavereason.LeaveReasonService;
import com.axelor.apps.hr.service.leavereason.LeaveReasonServiceImpl;
import com.axelor.apps.hr.service.lunch.voucher.LunchVoucherAdvanceService;
import com.axelor.apps.hr.service.lunch.voucher.LunchVoucherAdvanceServiceImpl;
import com.axelor.apps.hr.service.lunch.voucher.LunchVoucherExportService;
import com.axelor.apps.hr.service.lunch.voucher.LunchVoucherExportServiceImpl;
import com.axelor.apps.hr.service.lunch.voucher.LunchVoucherMgtLineService;
import com.axelor.apps.hr.service.lunch.voucher.LunchVoucherMgtLineServiceImpl;
import com.axelor.apps.hr.service.lunch.voucher.LunchVoucherMgtService;
import com.axelor.apps.hr.service.lunch.voucher.LunchVoucherMgtServiceImpl;
import com.axelor.apps.hr.service.move.MoveValidateHRServiceImpl;
import com.axelor.apps.hr.service.project.ProjectActivityDashboardServiceHRImpl;
import com.axelor.apps.hr.service.project.ProjectDashboardHRServiceImpl;
import com.axelor.apps.hr.service.project.ProjectPlanningTimeComputeNameService;
import com.axelor.apps.hr.service.project.ProjectPlanningTimeComputeNameServiceImpl;
import com.axelor.apps.hr.service.project.ProjectPlanningTimeService;
import com.axelor.apps.hr.service.project.ProjectPlanningTimeServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetCheckResponseService;
import com.axelor.apps.hr.service.timesheet.TimesheetCheckResponseServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetComputeNameService;
import com.axelor.apps.hr.service.timesheet.TimesheetComputeNameServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetContextProjectService;
import com.axelor.apps.hr.service.timesheet.TimesheetContextProjectServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetCreateService;
import com.axelor.apps.hr.service.timesheet.TimesheetCreateServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetDomainService;
import com.axelor.apps.hr.service.timesheet.TimesheetDomainServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetFetchService;
import com.axelor.apps.hr.service.timesheet.TimesheetFetchServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetInvoiceService;
import com.axelor.apps.hr.service.timesheet.TimesheetInvoiceServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetLeaveService;
import com.axelor.apps.hr.service.timesheet.TimesheetLeaveServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetLineCheckService;
import com.axelor.apps.hr.service.timesheet.TimesheetLineCheckServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetLineCreateService;
import com.axelor.apps.hr.service.timesheet.TimesheetLineCreateServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetLineGenerationService;
import com.axelor.apps.hr.service.timesheet.TimesheetLineGenerationServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetLineService;
import com.axelor.apps.hr.service.timesheet.TimesheetLineServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetLineUpdateService;
import com.axelor.apps.hr.service.timesheet.TimesheetLineUpdateServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetPeriodComputationService;
import com.axelor.apps.hr.service.timesheet.TimesheetPeriodComputationServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetProjectPlanningTimeService;
import com.axelor.apps.hr.service.timesheet.TimesheetProjectPlanningTimeServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetRemoveService;
import com.axelor.apps.hr.service.timesheet.TimesheetRemoveServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetReportService;
import com.axelor.apps.hr.service.timesheet.TimesheetReportServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetService;
import com.axelor.apps.hr.service.timesheet.TimesheetServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetTimeComputationService;
import com.axelor.apps.hr.service.timesheet.TimesheetTimeComputationServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetWorkflowCheckService;
import com.axelor.apps.hr.service.timesheet.TimesheetWorkflowCheckServiceImpl;
import com.axelor.apps.hr.service.timesheet.TimesheetWorkflowService;
import com.axelor.apps.hr.service.timesheet.TimesheetWorkflowServiceImpl;
import com.axelor.apps.hr.service.timesheet.timer.TimerTimesheetGenerationService;
import com.axelor.apps.hr.service.timesheet.timer.TimerTimesheetGenerationServiceImpl;
import com.axelor.apps.hr.service.timesheet.timer.TimesheetTimerCreateService;
import com.axelor.apps.hr.service.timesheet.timer.TimesheetTimerCreateServiceImpl;
import com.axelor.apps.hr.service.timesheet.timer.TimesheetTimerService;
import com.axelor.apps.hr.service.timesheet.timer.TimesheetTimerServiceImpl;
import com.axelor.apps.hr.service.user.UserHrService;
import com.axelor.apps.hr.service.user.UserHrServiceImpl;
import com.axelor.apps.project.db.repo.ProjectManagementRepository;
import com.axelor.apps.project.db.repo.ProjectPlanningTimeRepository;
import com.axelor.apps.project.db.repo.ProjectTaskProjectRepository;
import com.axelor.apps.project.service.ProjectActivityDashboardServiceImpl;
import com.axelor.apps.project.service.ProjectDashboardServiceImpl;

public class HumanResourceModule extends AxelorModule {

  @Override
  protected void configure() {

    bind(EmployeeService.class).to(EmployeeServiceImpl.class);
    bind(TimesheetService.class).to(TimesheetServiceImpl.class);
    bind(TimesheetLineService.class).to(TimesheetLineServiceImpl.class);
    bind(TimesheetTimerService.class).to(TimesheetTimerServiceImpl.class);
    bind(TimesheetRepository.class).to(TimesheetHRRepository.class);
    bind(TimesheetLineRepository.class).to(TimesheetLineHRRepository.class);
    bind(TSTimerRepository.class).to(TimesheetTimerHRRepository.class);
    bind(MailBatchService.class).to(MailBatchServiceHR.class);
    bind(AccountConfigService.class).to(AccountConfigHRService.class);
    bind(ExtraHoursService.class).to(ExtraHoursServiceImpl.class);
    bind(LunchVoucherMgtService.class).to(LunchVoucherMgtServiceImpl.class);
    bind(LunchVoucherMgtLineService.class).to(LunchVoucherMgtLineServiceImpl.class);
    bind(AppHumanResourceService.class).to(AppHumanResourceServiceImpl.class);
    bind(LunchVoucherAdvanceService.class).to(LunchVoucherAdvanceServiceImpl.class);
    bind(UserHrService.class).to(UserHrServiceImpl.class);
    bind(ExpenseRepository.class).to(ExpenseHRRepository.class);
    bind(ExpenseLineRepository.class).to(ExpenseLineHRRepository.class);
    bind(EmployeeRepository.class).to(EmployeeHRRepository.class);
    bind(BatchCreditTransferExpensePayment.class).to(BatchCreditTransferExpensePaymentHR.class);
    bind(BankOrderCancelServiceImpl.class).to(BankOrderCancelServiceHRImpl.class);
    bind(BankOrderLineOriginServiceImpl.class).to(BankOrderLineOriginServiceHRImpl.class);
    bind(HrBatchRepository.class).to(HrBatchHRRepository.class);
    bind(ProjectPlanningTimeRepository.class).to(ProjectPlanningTimeHRRepository.class);
    bind(ProjectPlanningTimeService.class).to(ProjectPlanningTimeServiceImpl.class);
    bind(ProjectManagementRepository.class).to(ProjectHRRepository.class);
    bind(ProjectTaskProjectRepository.class).to(ProjectTaskHRRepository.class);
    bind(UserBaseRepository.class).to(UserHRRepository.class);
    bind(PartnerAccountRepository.class).to(PartnerHRRepository.class);
    bind(BankOrderMergeServiceImpl.class).to(BankOrderMergeHRServiceImpl.class);
    bind(TimesheetReportService.class).to(TimesheetReportServiceImpl.class);
    bind(EmploymentContractRepository.class).to(EmploymentContractHRRepository.class);
    bind(AppTimesheetService.class).to(AppTimesheetServiceImpl.class);
    bind(EmploymentAmendmentTypeService.class).to(EmploymentAmendmentTypeServiceImpl.class);
    bind(ProjectDashboardServiceImpl.class).to(ProjectDashboardHRServiceImpl.class);
    bind(ProjectActivityDashboardServiceImpl.class).to(ProjectActivityDashboardServiceHRImpl.class);
    bind(AnalyticMoveLineGenerateRealServiceImpl.class)
        .to(AnalyticMoveLineGenerateRealServiceHrImpl.class);
    bind(ExpenseFetchPeriodService.class).to(ExpenseFetchPeriodServiceImpl.class);
    bind(TimesheetComputeNameService.class).to(TimesheetComputeNameServiceImpl.class);
    bind(MoveReverseServiceBankPaymentImpl.class).to(ExpenseMoveReverseServiceImpl.class);
    bind(ProjectPlanningTimeComputeNameService.class)
        .to(ProjectPlanningTimeComputeNameServiceImpl.class);
    bind(MoveValidateServiceImpl.class).to(MoveValidateHRServiceImpl.class);
    bind(HRDashboardService.class).to(HRDashboardServiceImpl.class);
    bind(ExpenseCancellationService.class).to(ExpenseCancellationServiceImpl.class);
    bind(ExpenseConfirmationService.class).to(ExpenseConfirmationServiceImpl.class);
    bind(ExpenseRefusalService.class).to(ExpenseRefusalServiceImpl.class);
    bind(ExpenseAnalyticService.class).to(ExpenseAnalyticServiceImpl.class);
    bind(ExpenseInvoiceLineService.class).to(ExpenseInvoiceLineServiceImpl.class);
    bind(ExpenseKilometricService.class).to(ExpenseKilometricServiceImpl.class);
    bind(ExpenseLineService.class).to(ExpenseLineServiceImpl.class);
    bind(ExpensePaymentService.class).to(ExpensePaymentServiceImpl.class);
    bind(ExpenseValidateService.class).to(ExpenseValidateServiceImpl.class);
    bind(ExpenseVentilateService.class).to(ExpenseVentilateServiceImpl.class);
    bind(ExpenseToolService.class).to(ExpenseToolServiceImpl.class);
    bind(ExpenseComputationService.class).to(ExpenseComputationServiceImpl.class);
    bind(EmployeeFileDMSService.class).to(EmployeeFileDMSServiceImpl.class);
    bind(ExpenseProofFileService.class).to(ExpenseProofFileServiceImpl.class);
    bind(LeaveLineService.class).to(LeaveLineServiceImpl.class);
    bind(LeaveRequestService.class).to(LeaveRequestServiceImpl.class);
    bind(LeaveRequestComputeDurationService.class).to(LeaveRequestComputeDurationServiceImpl.class);
    bind(LeaveRequestEventService.class).to(LeaveRequestEventServiceImpl.class);
    bind(LeaveRequestMailService.class).to(LeaveRequestMailServiceImpl.class);
    bind(LeaveRequestManagementService.class).to(LeaveRequestManagementServiceImpl.class);
    bind(LeaveRequestWorkflowService.class).to(LeaveRequestWorkflowServiceImpl.class);
    bind(ExpenseFetchMoveService.class).to(ExpenseFetchMoveServiceImpl.class);
    bind(ExpenseLineCreateService.class).to(ExpenseLineCreateServiceImpl.class);
    bind(EmployeeComputeStatusService.class).to(EmployeeComputeStatusServiceImpl.class);
    bind(MedicalVisitService.class).to(MedicalVisitServiceImpl.class);
    bind(MedicalVisitWorkflowService.class).to(MedicalVisitWorkflowServiceImpl.class);
    bind(MedicalVisitRepository.class).to(MedicalVisitHRRepository.class);
    bind(LeaveExportService.class).to(LeaveExportServiceImpl.class);
    bind(LunchVoucherExportService.class).to(LunchVoucherExportServiceImpl.class);
    bind(ExpenseCreateService.class).to(ExpenseCreateServiceImpl.class);
    bind(ExpensePrintService.class).to(ExpensePrintServiceImpl.class);
    bind(ExpenseLimitService.class).to(ExpenseLimitServiceImpl.class);
    bind(ExpenseLineResponseComputeService.class).to(ExpenseLineResponseComputeServiceImpl.class);
    bind(ExpenseLineCheckResponseService.class).to(ExpenseLineCheckResponseServiceImpl.class);
    bind(ExpenseCheckResponseService.class).to(ExpenseCheckResponseServiceImpl.class);
    bind(IncrementLeaveService.class).to(IncrementLeaveServiceImpl.class);
    bind(LeaveValueProrataService.class).to(LeaveValueProrataServiceImpl.class);
    bind(LeaveReasonService.class).to(LeaveReasonServiceImpl.class);
    bind(SchedulerCreationService.class).to(SchedulerCreationServiceImpl.class);
    bind(EmployeeFetchService.class).to(EmployeeFetchServiceImpl.class);
    bind(ExpenseCreateWizardService.class).to(ExpenseCreateWizardServiceImpl.class);
    bind(TimesheetContextProjectService.class).to(TimesheetContextProjectServiceImpl.class);
    bind(TimesheetCreateService.class).to(TimesheetCreateServiceImpl.class);
    bind(TimesheetDomainService.class).to(TimesheetDomainServiceImpl.class);
    bind(TimesheetFetchService.class).to(TimesheetFetchServiceImpl.class);
    bind(TimesheetInvoiceService.class).to(TimesheetInvoiceServiceImpl.class);
    bind(TimesheetLineGenerationService.class).to(TimesheetLineGenerationServiceImpl.class);
    bind(TimesheetPeriodComputationService.class).to(TimesheetPeriodComputationServiceImpl.class);
    bind(TimesheetProjectPlanningTimeService.class)
        .to(TimesheetProjectPlanningTimeServiceImpl.class);
    bind(TimesheetRemoveService.class).to(TimesheetRemoveServiceImpl.class);
    bind(TimesheetTimeComputationService.class).to(TimesheetTimeComputationServiceImpl.class);
    bind(TimesheetWorkflowService.class).to(TimesheetWorkflowServiceImpl.class);
    bind(TimesheetTimerCreateService.class).to(TimesheetTimerCreateServiceImpl.class);
    bind(TimerTimesheetGenerationService.class).to(TimerTimesheetGenerationServiceImpl.class);
    bind(TimesheetLineCreateService.class).to(TimesheetLineCreateServiceImpl.class);
    bind(TimesheetCheckResponseService.class).to(TimesheetCheckResponseServiceImpl.class);
    bind(TimesheetWorkflowCheckService.class).to(TimesheetWorkflowCheckServiceImpl.class);
    bind(TimesheetLineUpdateService.class).to(TimesheetLineUpdateServiceImpl.class);
    bind(TimesheetLineCheckService.class).to(TimesheetLineCheckServiceImpl.class);
    bind(ExpenseLineToolService.class).to(ExpenseLineToolServiceImpl.class);
    bind(ExpenseLineUpdateService.class).to(ExpenseLineUpdateServiceImpl.class);
    bind(BankOrderValidationServiceImpl.class).to(BankOrderValidationServiceHRImpl.class);
    bind(ICalendarEventObserver.class);
    bind(BankCardService.class).to(BankCardServiceImpl.class);
    bind(UnitConversionForProjectService.class).to(UnitConversionForProjectServiceImpl.class);
    bind(TimesheetLeaveService.class).to(TimesheetLeaveServiceImpl.class);
  }
}
