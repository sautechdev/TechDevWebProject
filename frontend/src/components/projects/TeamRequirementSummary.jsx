function TeamRequirementSummary({ roles, techFields }) {
  const fieldNames = new Map(techFields.map((field) => [String(field.id), field.name]));
  const total = roles.reduce((sum, role) => sum + (Number(role.requiredCount) || 0), 0);

  return <aside className="team-summary" aria-live="polite">
    <strong>Toplam İhtiyaç: {total} kişi</strong>
    <ul>{roles.filter((role) => role.techFieldId).map((role) =>
      <li key={role.key}>{fieldNames.get(String(role.techFieldId)) || 'Seçili alan'}: {Number(role.requiredCount) || 0} kişi</li>)}</ul>
  </aside>;
}

export default TeamRequirementSummary;
